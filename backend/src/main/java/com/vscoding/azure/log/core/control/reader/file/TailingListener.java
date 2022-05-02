package com.vscoding.azure.log.core.control.reader.file;

import com.vscoding.azure.log.core.entity.LogEntity;
import com.vscoding.azure.log.core.entity.LogRepository;
import com.vscoding.azure.log.core.entity.ReaderStateEntity;
import com.vscoding.azure.log.core.entity.ReaderStateRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
public class TailingListener extends TailerListenerAdapter {
  private final LogRepository logRepository;
  private final ReaderStateEntity state;
  private final String logPath;


  @Override
  public void handle(String line) {
    var logEntity = new LogEntity();

    logEntity.setFilename(logPath);
    logEntity.setLog(line);
    logEntity.setTimestamp(new Date());

    logRepository.save(logEntity);
  }

  @Override
  public void endOfFileReached() {
    this.state.setLastCheck(new Date());
  }

  @Override
  public void fileRotated() {
    this.state.setLastCheck(new Date());
  }
}
