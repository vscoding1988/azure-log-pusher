package com.vscoding.azure.log.core.control.parser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Will transform logs to Azure friendly format
 */
@Slf4j
@Service
public class SimpleLogParser {

  /**
   * Parse log line
   *
   * @return get named groups as map
   */
  public Map<String, String> parseLine(String line, SimpleLogConfig config) {
    var matcher = Pattern.compile(config.getPattern()).matcher(line);

    if (!matcher.matches()) {
      log.error("Could not parse '{}'", line);
      return null;
    }

    var values = new HashMap<String, String>();

    config.getFields().forEach(field -> {
      try {
        values.put(field, matcher.group(field));
      } catch (IllegalArgumentException e) {
        // TODO add option for skipping, or stopping
        log.info("Group '{}' not found.", field);
      }
    });

    return values;
  }
}
