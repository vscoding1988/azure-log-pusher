package com.vscoding.azure.log.core.control;

import com.google.gson.Gson;
import com.vscoding.azure.log.core.boundary.LogSendRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Will parse logs and send them as JSON to azure
 */
@Slf4j
@Service
@AllArgsConstructor
public class AzureService {

  private final AzureClient client;

  /**
   * Send logs to azure
   *
   * @param request {@link LogSendRequest} for sending logs
   * @return send status
   */
  public boolean sendLogs(LogSendRequest request) {
    log.info("Start log sending");

    try {
      var logs = new AzureLogParser(request).parseLogs(request.getPath());
      log.info("{} log entries found and parsed", logs.size());

      return client.sendLogs(getLogName(request.getPath()), new Gson().toJson(logs));
    } catch (Exception e) {
      log.error("Could not parse logs", e);
    }
    return false;
  }

  /**
   * Get log name from log file path
   *
   * @param logPath path to the log
   * @return processed log name
   */
  private String getLogName(String logPath) {
    var segments = logPath.split("/");

    var fileName = segments[segments.length - 1];

    // only [a-zA-Z0-9_] are allowed
    fileName = fileName.replaceAll("[^a-zA-Z0-9_]", "_");

    // Max length is capped to 100
    if (fileName.length() >= 100) {
      fileName = fileName.substring(0, 99);
    }

    return fileName;
  }
}
