package com.vscoding.azure.log.core.control.parser;

import com.google.gson.Gson;
import com.vscoding.azure.log.core.control.reader.file.TailingFileConfig;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LogParser {
  private final SimpleLogParser parser;
  private final DateProcessor processor;

  /**
   * Parse log line
   *
   * @param line log line
   * @param config {@link TailingFileConfig} configuration for parsing line
   * @return log in azure json format
   */
  public String parse(String line, TailingFileConfig config) {
    var values = parser.parseLine(line, config);

    processor.process(values, config);

    return new Gson().toJson(values);
  }
}
