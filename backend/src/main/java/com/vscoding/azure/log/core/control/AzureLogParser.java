package com.vscoding.azure.log.core.control;

import com.google.gson.JsonObject;
import com.vscoding.azure.log.core.boundary.LogSendRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Will transform logs to Azure friendly format
 */
@Slf4j
public class AzureLogParser {

  private final Pattern logPattern;
  private final String timestampPattern;
  private final String timestampFieldName;
  private final List<String> logFields;

  public AzureLogParser(LogSendRequest request) {
    this.logPattern = Pattern.compile(request.getPattern());
    this.logFields = request.getFields();
    this.timestampPattern = request.getTimestampPattern();
    this.timestampFieldName = request.getTimestampFieldName();
  }

  /**
   * Parse logs for given location line by line
   *
   * @return {@link  JsonObject} list
   */
  public List<JsonObject> parseLogs(String logPath) throws IOException {
    var logs = new ArrayList<JsonObject>();
    var resourceAsStream = new FileInputStream(logPath);

    try (var br = new BufferedReader(new InputStreamReader(resourceAsStream))) {

      br.lines()
              .map(this::parseLine)
              .filter(Objects::nonNull)
              .forEach(logs::add);
    } catch (Exception e) {
      log.error("Error parsing file '{}'", logPath, e);
    }

    return logs;
  }

  /**
   * Parse log line
   *
   * @return {@link JsonObject} as line representation, null if line is unparseable
   */
  private JsonObject parseLine(String line) {
    var matcher = logPattern.matcher(line);

    if (!matcher.matches()) {
      log.error("Could not parse '{}'", line);
      return null;
    }

    var values = new JsonObject();

    for (var logField : logFields) {
      var value = matcher.group(logField);

      // change date to Azure friendly pattern
      if (logField.equals(timestampFieldName)) {
        value = getAzureDate(value);
      }

      values.addProperty(logField, value);
    }

    return values;
  }

  /**
   * Change log date to ISO 8601 format
   *
   * @param date date string with pattern defined in timestampPattern
   * @return dateString in ISO 8601 format or if parsing not possible the original value
   */
  private String getAzureDate(String date) {
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
