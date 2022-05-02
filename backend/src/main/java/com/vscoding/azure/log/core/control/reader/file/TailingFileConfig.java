package com.vscoding.azure.log.core.control.reader.file;

import com.vscoding.azure.log.core.control.parser.SimpleDateConfig;
import com.vscoding.azure.log.core.control.parser.SimpleLogConfig;
import com.vscoding.azure.log.core.control.reader.ReaderConfig;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class TailingFileConfig implements SimpleLogConfig, SimpleDateConfig, ReaderConfig {
  @NotBlank
  private String path;
  @NotBlank
  private String pattern;
  private String timestampFieldName;
  private String timestampPattern;
  @NotEmpty
  private List<String> fields;
}
