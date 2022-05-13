package com.vscoding.azure.log.core.control.parser;

/**
 * Configuration for parsing date fields, currently supporting only one field (the date of the log)
 */
public interface SimpleDateConfig {

  /**
   * Name of the log date field
   *
   * @return name of the field
   */
  String getTimestampFieldName();

  /**
   * Date pattern used in the log.
   *
   * @return date pattern
   */
  String getTimestampPattern();
}
