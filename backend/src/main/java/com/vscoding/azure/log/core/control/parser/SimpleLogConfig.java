package com.vscoding.azure.log.core.control.parser;

import java.util.List;

public interface SimpleLogConfig {
  String getPattern();

  List<String> getFields();
}
