package com.vscoding.azure.log.core.control;

import com.vscoding.azure.log.core.control.reader.file.TailingFileConfig;
import com.vscoding.azure.log.core.control.reader.file.TailingFileReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Will parse logs and send them as JSON to azure
 */
@Slf4j
@Service
@AllArgsConstructor
public class AzureService {

  private final TailingFileReader fileReader;

  /**
   * Register a file log reader
   *
   * @param config file config
   */
  public void registerLogReader(TailingFileConfig config) {
    fileReader.run(config);
  }
}
