package com.vscoding.azure.log.core.control.client;

import com.google.gson.Gson;
import com.vscoding.azure.log.core.control.parser.LogParser;
import com.vscoding.azure.log.core.control.reader.ReaderConfig;
import com.vscoding.azure.log.core.entity.LogEntity;
import com.vscoding.azure.log.core.entity.LogRepository;
import com.vscoding.azure.log.core.entity.ReaderStateRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * The service will collected logs from the database, process and push them to the azure service,
 * afterwords the logs are deleted
 */
@Slf4j
@Service
public class AzureSendService {

  private final ReaderStateRepository stateRepository;
  private final LogRepository logRepository;
  private final LogParser logParser;
  private final AzureClient azureClient;
  private final int size;

  public AzureSendService(LogRepository logRepository,
          ReaderStateRepository stateRepository,
          LogParser logParser,
          AzureClient azureClient,
          @Value("${app.client.bucketSize}") int size) {
    this.logRepository = logRepository;
    this.stateRepository = stateRepository;
    this.logParser = logParser;
    this.azureClient = azureClient;
    this.size = size;
  }

  /**
   * Get the newest logs and push them to azure
   */
  @Scheduled(cron = "30 * * * * *")
  protected void push() {
    log.info("Found {} new log to send.", logRepository.count());
    var logsByReader = getLogsByReaderId();
    var configByReader = getReaderConfigs(logsByReader.keySet());

    logsByReader.forEach((readerId, logs) -> {
      var config = configByReader.get(readerId);

      if (config != null) {
        log.info("Start processing {} logs for '{}'", logs.size(), config.getPath());

        // TODO embed buckets, to speed up log parsing, by not recompiling Pattern
        var parsedLogs = logs.stream()
                .map(LogEntity::getLog)
                .map(line -> logParser.parse(line, config))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        var json = new Gson().toJson(parsedLogs);
        azureClient.sendLogs(config.getPath(), config.getLogName(), json);
      } else {
        log.error("Could not find config with ID '{}', logs will be dropped.", readerId);
      }
      logRepository.deleteAll(logs);
    });
  }

  /**
   * Create lookup for {@link ReaderConfig} by reader id
   * TODO: keep already computed configs
   *
   * @param readerIds relevant reader ids
   * @return look up
   */
  private Map<String, ReaderConfig> getReaderConfigs(Set<String> readerIds) {
    var configByReaderId = new HashMap<String, ReaderConfig>();

    stateRepository.findAllById(readerIds).forEach(state -> {
      var config = new Gson().fromJson(state.getConfig(), state.getConfigClass());
      configByReaderId.put(state.getId(), config);
    });

    return configByReaderId;
  }

  /**
   * Get oldest logs grouped bei reader id
   *
   * @return lsit of {@link LogEntity} grouped by reader id
   */
  private Map<String, List<LogEntity>> getLogsByReaderId() {
    var logsByReader = new HashMap<String, List<LogEntity>>();
    var request = PageRequest.of(0, size, Sort.by(Sort.Direction.ASC, "timestamp"));

    logRepository
            .findAll(request)
            .forEach(log -> logsByReader
                    .computeIfAbsent(log.getReaderId(), name -> new ArrayList<>()).add(log));

    return logsByReader;
  }
}
