package com.vscoding.azure.log.core.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * Representation of parsed and sent log to azure
 */
@Entity
@Getter
@Setter
public class LogEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  /**
   * Time stamp of creation of the entity, NOT of the log
   */
  private Date timestamp;

  /**
   * log entry
   */
  @Lob
  private String log;

  /**
   * Name of the file, this log belongs to
   */
  private String filename;
}
