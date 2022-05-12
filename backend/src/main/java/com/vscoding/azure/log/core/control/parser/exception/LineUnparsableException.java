package com.vscoding.azure.log.core.control.parser.exception;

/**
 * Thrown when line cannot be parsed by logger
 */
public class LineUnparsableException extends RuntimeException {

  public LineUnparsableException(String message) {
    super(message);
  }
}
