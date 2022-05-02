package com.vscoding.azure.log.core.control.reader.file;

import com.vscoding.azure.log.core.entity.LogRepository;
import com.vscoding.azure.log.core.entity.ReaderStateRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.Tailer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * This reader will trail given file and push every minute the files to the sending que
 */
@Slf4j
@Service
public class TailingFileReader {

  private final long tailingDelay;
  private final LogRepository logRepository;
  private final ReaderStateRepository stateRepository;

  public TailingFileReader(@Value("${app.reader.file.delay:1000}") long tailingDelay, LogRepository logRepository, ReaderStateRepository stateRepository) {
    this.tailingDelay = tailingDelay;
    this.logRepository = logRepository;
    this.stateRepository = stateRepository;
  }

  public void run(TailingFileConfig config) {

    try {
      var listener = new TailingListener(logRepository, stateRepository, config);
      var tailer = Tailer.create(new File(config.getPath()), listener, tailingDelay);

      var thread = new Thread(tailer);
      thread.setDaemon(true);
      thread.start();
    } catch (Exception e) {
      log.error("Failed with exception:", e);
    }
  }
}
