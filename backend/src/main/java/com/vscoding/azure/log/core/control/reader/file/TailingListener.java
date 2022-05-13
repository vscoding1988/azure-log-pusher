package com.vscoding.azure.log.core.control.reader.file;

import com.vscoding.azure.log.core.entity.LogEntity;
import com.vscoding.azure.log.core.entity.LogRepository;
import com.vscoding.azure.log.core.entity.ProcessedLog;
import com.vscoding.azure.log.core.entity.ProcessedLogRepository;
import com.vscoding.azure.log.core.entity.ReaderStateEntity;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.TailerListenerAdapter;

/**
 * Tailing listener, will read file line by line and persist each line in the database
 */
@Slf4j
@AllArgsConstructor
public class TailingListener extends TailerListenerAdapter {

  private final LogRepository logRepository;
  private final ProcessedLogRepository processedLogRepository;
  private final ReaderStateEntity state;
  private final String logPath;
  private final List<LogEntity> buffer = new ArrayList<>();

  /**
   * Process a line, first check if the log is already processed
   *
   * @param line log line the reader is currently sending
   */
  @Override
  public void handle(String line) {
    if (isNewLine(line)) {
      createLog(line);
    }
  }

  /**
   * Persist the line as {@link LogEntity}
   *
   * @param line log line currently processed
   */
  private void createLog(String line) {
    var logEntity = new LogEntity();

    logEntity.setFilename(logPath);
    logEntity.setLog(line);
    logEntity.setTimestamp(new Date());
    logEntity.setReaderId(state.getId());

    buffer.add(logEntity);
  }

  /**
   * Check if this line was processed before
   *
   * @param line log line
   * @return true if the line is new
   */
  private boolean isNewLine(String line) {
    try {
      var log = ProcessedLog.getInstance(line, logPath);

      if (!processedLogRepository.existsById(log.getId())) {
        processedLogRepository.save(log);
        return true;
      }
      return false;
    } catch (Exception e) {
      log.error("Could not create log line.", e);
    }
    return true;
  }

  @Override
  public void endOfFileReached() {
    logRepository.saveAll(buffer);
    this.state.setLastCheck(new Date());
    this.buffer.clear();
  }

  @Override
  public void fileRotated() {
    endOfFileReached();
  }
}
