package com.vscoding.azure.log.core.control.reader.file;

import com.vscoding.azure.log.core.entity.LogEntity;
import com.vscoding.azure.log.core.entity.LogRepository;
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
  private final ReaderStateEntity state;
  private final String logPath;
  private final List<LogEntity> buffer = new ArrayList<>();

  @Override
  public void handle(String line) {
    var logEntity = new LogEntity();

    logEntity.setFilename(logPath);
    logEntity.setLog(line);
    logEntity.setTimestamp(new Date());
    logEntity.setReaderId(state.getId());

    buffer.add(logEntity);
  }

  @Override
  public void endOfFileReached() {
    logRepository.saveAll(buffer);
    this.state.setLastCheck(new Date());
  }

  @Override
  public void fileRotated() {
    endOfFileReached();
  }
}
