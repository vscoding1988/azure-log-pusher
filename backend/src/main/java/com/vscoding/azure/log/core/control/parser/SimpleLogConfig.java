package com.vscoding.azure.log.core.control.parser;

/**
 * Configuration for regex log parsing
 */
public interface SimpleLogConfig {

  /**
   * Regex pattern for the log
   *
   * @return log pattern
   */
  String getPattern();
}
