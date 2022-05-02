package com.vscoding.azure.log.core.control.client;

import com.google.gson.Gson;
import com.vscoding.azure.log.core.control.parser.LogParser;
import com.vscoding.azure.log.core.entity.LogEntity;
import com.vscoding.azure.log.core.entity.LogRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class AzureSendService {
  private final LogRepository logRepository;
  private final LogParser logParser;
  private final AzureClient azureClient;
  private final int size;

  public AzureSendService(LogRepository logRepository,
                          LogParser logParser,
                          AzureClient azureClient,
                          @Value("${app.client.bucketSize}") int size) {
    this.logRepository = logRepository;
    this.logParser = logParser;
    this.azureClient = azureClient;
    this.size = size;
  }

  protected void push() {
    var request = PageRequest.of(0, size, Sort.by(Sort.Direction.ASC, "timestamp"));

    var logsByFileName = new HashMap<String, List<LogEntity>>();

    logRepository.findAll(request)
            .forEach(log -> logsByFileName.computeIfAbsent(log.getFilename(), (name) -> new ArrayList<>()).add(log));

    logsByFileName.forEach((filename, logs) -> {
      logs.stream().map(LogEntity::getLog).map(logParser::parse);
      var json = new Gson().toJson(logs);
      azureClient.sendLogs(filename, json);
    });
  }
}
