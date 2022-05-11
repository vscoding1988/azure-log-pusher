package com.vscoding.azure.log.core.control;

import com.google.gson.Gson;
import com.vscoding.azure.log.core.control.reader.ReaderConfig;
import com.vscoding.azure.log.core.entity.ReaderStateEntity;
import com.vscoding.azure.log.core.entity.ReaderStateRepository;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Basic service for Controller requests
 */
@Slf4j
@Service
@AllArgsConstructor
public class LogService {

  private final ReaderStateRepository stateRepository;

  /**
   * Create {@link ReaderStateEntity} in the database based on user request
   *
   * @param config file config
   */
  public void registerLogReader(ReaderConfig config) {
    var state = new ReaderStateEntity();

    state.setId(UUID.randomUUID().toString());
    state.setConfig(new Gson().toJson(config));
    state.setConfigClass(config.getClass());

    stateRepository.save(state);
  }
}
