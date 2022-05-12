package com.vscoding.azure.log.core.control.parser;

import com.vscoding.azure.log.core.control.parser.exception.LogParserException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Will transform logs to Azure friendly format
 */
@Slf4j
@Service
public class SimpleLogParser {

  private static final Pattern FIELD_PATTERN = Pattern.compile("\\(\\?<(.+?)>");

  public List<Map<String, String>> parseLines(List<String> lines, String regex) {
    var pattern = Pattern.compile(regex);
    var fields = getFields(regex);

    return lines.stream().map(line -> {
              try {
                return parseLine(line,fields, pattern);
              } catch (LogParserException e) {
                log.warn(e.getMessage());
              }
              return new HashMap<String,String>();
            }).filter(map -> !map.isEmpty())
            .toList();
  }

  /**
   * Parse log line
   *
   * @return get named groups as map
   */
  private Map<String, String> parseLine(String line, List<String> fields, Pattern pattern) {
    var matcher = pattern.matcher(line);

    if (!matcher.matches()) {
      throw new LogParserException("Could not parse '" + line + "'");
    }

    var values = new HashMap<String, String>();

    fields.forEach(field -> values.put(field, matcher.group(field)));

    if (values.isEmpty()) {
      throw new LogParserException("Could not parse '" + line + "'");
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
