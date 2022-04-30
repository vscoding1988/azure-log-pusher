package com.vscoding.azure.log.core.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Representation of parsed and sent log to azure
 */
@Entity
public class LogEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  /**
   * Log timestamp
   */
  private long timestamp;

  /**
   * Hash of the log
   */
  private String hash;

  /**
   * Name of the file, this log belongs to
   */
  private String filename;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }
}
