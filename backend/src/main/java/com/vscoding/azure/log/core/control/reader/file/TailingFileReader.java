package com.vscoding.azure.log.core.control.reader.file;

import com.google.gson.Gson;
import com.vscoding.azure.log.core.entity.LogRepository;
import com.vscoding.azure.log.core.entity.ReaderStateEntity;
import com.vscoding.azure.log.core.entity.ReaderStateRepository;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.Tailer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * This reader will trail given file and push every minute the files to the sending que
 */
@Slf4j
@Service
public class TailingFileReader {

  private final long tailingDelay;
  private final LogRepository logRepository;
  private final ReaderStateRepository stateRepository;
  private final List<ReaderStateEntity> currentRunning = new ArrayList<>();

  public TailingFileReader(@Value("${app.reader.file.delay:1000}") long tailingDelay,
          LogRepository logRepository, ReaderStateRepository stateRepository) {
    this.tailingDelay = tailingDelay;
    this.logRepository = logRepository;
    this.stateRepository = stateRepository;
  }

  /**
   * Searches for new reader and starts them
   */
  @Scheduled(cron = "0 * * * * *")
  protected void startNewReader() {
    stateRepository.findAllByLastCheckIsNullAndConfigClass(TailingFileConfig.class).forEach(this::run);

    // To reduce DB write access, write only once a minute
    stateRepository.saveAll(currentRunning);
  }

  /**
   * Start new {@link TailingListener} for created state
   *
   * @param state {@link ReaderStateEntity} not started reader
   */
  private void run(ReaderStateEntity state) {

    try {
      var config = new Gson().fromJson(state.getConfig(), TailingFileConfig.class);

      var listener = new TailingListener(logRepository, state, config.getPath());
      var tailer = Tailer.create(new File(config.getPath()), listener, tailingDelay);

      var thread = new Thread(tailer);
      thread.setDaemon(true);
      thread.start();

      currentRunning.add(state);
    } catch (Exception e) {
      log.error("Failed with exception:", e);
    }
  }
}
