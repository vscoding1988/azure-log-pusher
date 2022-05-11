package com.vscoding.azure.log.core.control.client;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Based on implementation provided in https://docs.microsoft.com/en-us/azure/azure-monitor/logs/data-collector-api#java-sample
 */
@Slf4j
@Service
public class AzureClient {

  private static final String AUTHORISATION_HEADER = "POST\n%s\napplication/json\nx-ms-date:%s\n/api/logs";
  private static final String URL = "https://%s.ods.opinsights.azure.com/api/logs?api-version=2016-04-01";
  private static final String RFC_1123_DATE = "EEE, dd MMM yyyy HH:mm:ss z";

  /**
   * Log analytics workspace id
   */
  private final String workspaceId;

  /**
   * Use either the primary or the secondary Connected Sources client authentication key
   */
  private final String connectionKey;

  public AzureClient(
          @Value("${app.azure.workspaceId}") String workspaceId,
          @Value("${app.azure.connectionKey}") String connectionKey) {
    this.workspaceId = workspaceId;
    this.connectionKey = connectionKey;
  }

  /**
   * Will send logs to azure
   *
   * @param logPath       log path
   * @param customLogName name of the azure_log
   * @param logs          stringified logs to send
   * @return sending successful
   */
  public boolean sendLogs(String logPath, String customLogName, String logs) {
    log.info("Start sending logs for '{}'", logPath);
    var logName = StringUtils.hasText(customLogName) ? customLogName : getLogName(logPath);

    var cleanLogName = logName.replaceAll("[^a-zA-Z0-9_]", "_");

    try (var httpClient = HttpClients.createDefault()) {
      var httpPost = getPost(cleanLogName, logs);
      var response = httpClient.execute(httpPost);
      var statusCode = response.getStatusLine().getStatusCode();

      if (statusCode != 200) {
        log.warn("Error sending logs to azure, status code: {}", statusCode);
        return false;
      }

      return true;
    } catch (Exception e) {
      log.error("Error sending request for '{}' to azure", cleanLogName, e);
    }

    return false;
  }

  /**
   * Build azure post request
   *
   * @param logName name of the log, the name will be used for creating custom log in azure
   * @param logs    stringified logs to send
   * @return {@link HttpPost} request to azure
   * @throws Exception exceptions for building logs
   */
  private HttpPost getPost(String logName, String logs) throws Exception {
    var url = String.format(URL, workspaceId);
    var date = getServerTime();
    var httpPost = new HttpPost(url);

    httpPost.setHeader("Authorization", getAuthorisation(date, logs));
    httpPost.setHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    httpPost.setHeader("Log-Type", logName);
    httpPost.setHeader("x-ms-date", date);
    httpPost.setEntity(new StringEntity(logs));

    return httpPost;
  }

  /**
   * Get server time for header
   *
   * @return server time as RFC_1123 date
   */
  private String getServerTime() {
    var calendar = Calendar.getInstance();
    var dateFormat = new SimpleDateFormat(RFC_1123_DATE, Locale.US);
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    return dateFormat.format(calendar.getTime());
  }

  /**
   * Will create the authorisation header value
   *
   * @param date request date
   * @param logs stringified logs to send
   * @return authorisation string
   * @throws Exception building exceptions
   */
  private String getAuthorisation(String date, String logs) throws Exception {
    var stringToHash = String.format(AUTHORISATION_HEADER,
            logs.getBytes(StandardCharsets.UTF_8).length,
            date);

    var hashedString = getHMAC256(stringToHash);
    return "SharedKey " + workspaceId + ":" + hashedString;
  }

  /**
   * Will create a hashedString for authorisation
   *
   * @param input AUTHORISATION_HEADER with values
   * @return hashed HmacSHA256 string
   * @throws NoSuchAlgorithmException HmacSHA256 not found
   * @throws InvalidKeyException      encoding exceptions
   */
  private String getHMAC256(String input) throws NoSuchAlgorithmException, InvalidKeyException {
    var sha256HMAC = Mac.getInstance("HmacSHA256");
    var secretKey = new SecretKeySpec(
            Base64.getDecoder().decode(connectionKey.getBytes(StandardCharsets.UTF_8)),
            "HmacSHA256");
    sha256HMAC.init(secretKey);

    return new String(
            Base64.getEncoder().encode(sha256HMAC.doFinal(input.getBytes(StandardCharsets.UTF_8))));
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
