package com.vscoding.azure.log.core.control.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import org.junit.jupiter.api.Test;

class SimpleLogParserTest {

  final SimpleLogParser sut = new SimpleLogParser();

  @Test
  void parseLine() {
    // Given
    var line = "PUT \"https://domain/path\" 200";
    var pattern = "(?<Method>[A-Z]+) \"(?<Path>.[^\"]+)\" (?<StatusCode>\\d+)";

    // When
    var values = sut.parseLine(line, pattern);

    // Then
    assertEquals("PUT", values.get("Method"));
    assertEquals("https://domain/path", values.get("Path"));
    assertEquals("200", values.get("StatusCode"));
  }

  @Test
  void parseLine_withoutGroups() {
    // Given
    var line = "PUT \"https://domain/path\" 200";
    var pattern = "([A-Z]+) \"(.[^\"]+)\" (\\d+)";

    // When
    assertThrows(LogParserException.class, () -> sut.parseLine(line, pattern));
  }

  @Test
  void parseLine_unParsable() {
    // Given
    var line = "PUT \"https://domain/path\" 200";
    var pattern = "([1-9]+) \"(.[^\"]+)\" (\\d+)";

    // When
    var values = sut.parseLine(line, pattern);

    // Then
    assertNull(values);
  }
}
