package com.vscoding.azure.log.core.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;


/**
 * Represents the reader state
 */
@Entity
@Getter
@Setter
public class ReaderStateEntity {
  /**
   * Each reader will generate own id, and keep it as local variable
   */
  @Id
  private String id;

  /**
   * Current state of the reader, if false the reader is sleeping
   */
  private boolean running;

  /**
   * Does reader has errors
   */
  private boolean error;

  /**
   * Last reader execution (f.e. checking the log file)
   */
  private Date lastCheck;

  /**
   * Path to the log (or url, ect) used for display only
   */
  private String path;

}
