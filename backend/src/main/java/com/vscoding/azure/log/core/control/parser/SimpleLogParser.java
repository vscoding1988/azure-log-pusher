package com.vscoding.azure.log.core.control.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Will transform logs to Azure friendly format
 */
@Slf4j
@Service
public class SimpleLogParser {

  private static final Pattern FIELD_PATTERN = Pattern.compile("\\(\\?<(.+?)>");

  /**
   * Parse log line
   *
   * @return get named groups as map
   */
  public Map<String, String> parseLine(String line, SimpleLogConfig config) {
    return parseLine(line, config.getPattern());
  }

  public Map<String, String> parseLine(String line, String pattern) {
    var matcher = Pattern.compile(pattern).matcher(line);

    if (!matcher.matches()) {
      log.error("Could not parse '{}'", line);
      return null;
    }

    var values = new HashMap<String, String>();

    getFields(pattern).forEach(field -> {
      try {
        values.put(field, matcher.group(field));
      } catch (IllegalArgumentException e) {
        // TODO add option for skipping, or stopping
        log.info("Group '{}' not found.", field);
      }
    });

    if (values.isEmpty()) {
      log.warn("Could not parse '{}'", line);
      return null;
    }

    return values;
  }

  /**
   * Will parse log fields out of the provided pattern.
   *
   * @param pattern log pattern
   * @return list of found fields
   */
  private List<String> getFields(String pattern) {
    var fields = new ArrayList<String>();
    var matcher = FIELD_PATTERN.matcher(pattern);

    while (matcher.find()) {
      for (int i = 1; i <= matcher.groupCount(); i++) {
        fields.add(matcher.group(i));
      }
    }

    if (fields.isEmpty()) {
      throw new LogParserException("The pattern '" + pattern + "' does not contain named groups.");
    }

    return fields;
  }
}
