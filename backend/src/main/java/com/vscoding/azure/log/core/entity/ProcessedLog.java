package com.vscoding.azure.log.core.entity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.binary.Hex;

/**
 * Represents already processed log
 */
@Entity
@Getter
@Setter
public class ProcessedLog {

  /**
   * Concat log and log path and hash them with MD4
   */
  @Id
  private String id;

  private ProcessedLog(String id) {
    this.id = id;
  }

  protected ProcessedLog() {
  }

  /**
   * Will create a {@link ProcessedLog} instance for given log line and reader id
   *
   * @param log     log line which is processed
   * @param logPath log path
   * @return {@link ProcessedLog}
   * @throws NoSuchAlgorithmException thrown when MD5 is not found
   */
  public static ProcessedLog getInstance(String log, String logPath)
          throws NoSuchAlgorithmException {
    var unhashedId = log + logPath;

    var crypt = MessageDigest.getInstance("MD5");
    crypt.update(unhashedId.getBytes(StandardCharsets.UTF_8));

    return new ProcessedLog(Hex.encodeHexString(crypt.digest()));
  }
}
