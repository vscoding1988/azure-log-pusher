package com.vscoding.azure.log.core.boundary;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class LogSendRequest {
  @NotBlank
  private String path;
  @NotBlank
  private String pattern;
  private String timestampFieldName;
  private String timestampPattern;
  @NotEmpty
  private List<String> fields;
}
