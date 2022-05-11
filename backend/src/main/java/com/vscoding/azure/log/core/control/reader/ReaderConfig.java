package com.vscoding.azure.log.core.control.reader;

/**
 * Base configuration for all reader
 */
public interface ReaderConfig {

  /**
   * Path is used for sending to azure, it can be a file path or an url
   *
   * @return log path
   */
  String getPath();

  String getLogName();
}
