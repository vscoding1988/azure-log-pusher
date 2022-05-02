package com.vscoding.azure.log.core.control.parser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Change Date values to azure consumable date format
 */
@Slf4j
@Service
public class DateProcessor {

  public void process(Map<String, String> values, SimpleDateConfig config) {
    Stream.of(config.getTimestampFieldName())
            .filter(values::containsKey)
            .forEach(field -> values.put(field, getAzureDate(values.get(field), config)));
  }

  /**
   * Change log date to ISO 8601 format
   *
   * @param date date string with pattern defined in timestampPattern
   * @return dateString in ISO 8601 format or if parsing not possible the original value
   */
  private String getAzureDate(String date, SimpleDateConfig config) {
    var timestampPattern = config.getTimestampPattern();
    var azureDate = date;

    if (timestampPattern != null && !timestampPattern.isEmpty()) {
      try {
        var parsedDate = new SimpleDateFormat(timestampPattern, Locale.ENGLISH).parse(date);
        azureDate = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ", Locale.ENGLISH).format(parsedDate);
      } catch (Exception e) {
        log.error("Could not parse date '{}' with pattern '{}'", date, timestampPattern);
      }
    }

    return azureDate;
  }
}
