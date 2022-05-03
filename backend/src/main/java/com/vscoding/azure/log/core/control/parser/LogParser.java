package com.vscoding.azure.log.core.control.parser;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.vscoding.azure.log.core.control.reader.ReaderConfig;
import com.vscoding.azure.log.core.control.reader.file.TailingFileConfig;
import java.util.Optional;
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
   * @param line   log line
   * @param config {@link TailingFileConfig} configuration for parsing line
   * @return log in azure json format
   */
  public Optional<JsonElement> parse(String line, ReaderConfig config) {

    if (config instanceof SimpleLogConfig logConfig) {
      var values = parser.parseLine(line, logConfig);

      if (values == null) {
        // TODO: better handling for not parseable lines
        return Optional.empty();
      }
      if (config instanceof SimpleDateConfig dateConfig) {
        processor.process(values, dateConfig);
      }
      return Optional.of(new Gson().toJsonTree(values));
    }

    return Optional.empty();
  }
}
