package com.vscoding.azure.log.core.entity;

import com.vscoding.azure.log.core.control.reader.ReaderConfig;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import lombok.Getter;
import lombok.Setter;


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
   * Config in json format
   */
  @Lob
  private String config;

  /**
   * Class for deserialization of the config
   */
  private Class<? extends ReaderConfig> configClass;

}
