package com.vscoding.azure.log.core.control.parser;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.vscoding.azure.log.core.control.reader.ReaderConfig;
import com.vscoding.azure.log.core.control.reader.file.TailingFileConfig;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class LogParser {

  private final SimpleLogParser parser;
  private final DateProcessor processor;

  /**
   * Parse log line
   *
   * @param lines  log lines
   * @param config {@link TailingFileConfig} configuration for parsing line
   * @return log in azure json format
   */
  public JsonElement parse(List<String> lines, ReaderConfig config) {
    if (config instanceof SimpleLogConfig logConfig) {
      var values = parser.parseLines(lines, logConfig.getPattern());

      if (config instanceof SimpleDateConfig dateConfig) {
        values.forEach(value -> processor.process(value, dateConfig));
      }
      return new Gson().toJsonTree(values);
    }
    return null;
  }
}
