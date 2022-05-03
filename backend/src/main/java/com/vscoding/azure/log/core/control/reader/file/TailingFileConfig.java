package com.vscoding.azure.log.core.control.reader.file;

import com.vscoding.azure.log.core.control.parser.SimpleDateConfig;
import com.vscoding.azure.log.core.control.parser.SimpleLogConfig;
import com.vscoding.azure.log.core.control.reader.ReaderConfig;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Configuration for file tailing
 */
@Data
public class TailingFileConfig implements SimpleLogConfig, SimpleDateConfig, ReaderConfig {
  /**
   * Path to the log file
   */
  @NotBlank
  private String path;

  /**
   * Log pattern
   */
  @NotBlank
  private String pattern;

  /**
   * Date field name
   */
  private String timestampFieldName;

  /**
   * Log date pattern
   */
  private String timestampPattern;

  /**
   * Fields, which should be sent to azure, the name should match the pattern named groups
   */
  @NotEmpty
  private List<String> fields;
}
