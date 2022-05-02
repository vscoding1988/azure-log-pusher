package com.vscoding.azure.log.core.control.reader.file;

import com.vscoding.azure.log.core.entity.LogEntity;
import com.vscoding.azure.log.core.entity.LogRepository;
import com.vscoding.azure.log.core.entity.ReaderStateEntity;
import com.vscoding.azure.log.core.entity.ReaderStateRepository;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import java.util.Date;
import java.util.UUID;

public class TailingListener extends TailerListenerAdapter {
  private final LogRepository logRepository;
  private final ReaderStateRepository stateRepository;
  private final TailingFileConfig config;
  private ReaderStateEntity state;

  public TailingListener(LogRepository logRepository, ReaderStateRepository stateRepository, TailingFileConfig config) {
    this.logRepository = logRepository;
    this.stateRepository = stateRepository;
    this.config = config;
  }

  @Override
  public void init(Tailer tailer) {
    super.init(tailer);
    this.state = createState();
  }

  public void handle(String line) {
    var logEntity = new LogEntity();

    logEntity.setFilename(config.getPath());
    logEntity.setLog(line);
    logEntity.setTimestamp(new Date());

    logRepository.save(logEntity);
    // TODO date change for state (f.e. once a minute)
  }

  @Override
  public void endOfFileReached() {
    this.state.setLastCheck(new Date());
    stateRepository.save(state);
  }

  @Override
  public void fileRotated() {
    this.state.setLastCheck(new Date());
    stateRepository.save(state);
  }

  /**
   * Create state for log
   *
   * @return {@link ReaderStateEntity} for the run
   */
  private ReaderStateEntity createState() {
    var state = new ReaderStateEntity();

    state.setId(UUID.randomUUID().toString());
    state.setPath(config.getPath());
    stateRepository.save(state);

    return state;
  }
}
