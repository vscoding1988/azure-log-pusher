package com.vscoding.azure.log.core.control.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.vscoding.azure.log.core.control.parser.exception.LogParserException;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class SimpleLogParserTest {

  final SimpleLogParser sut = new SimpleLogParser();

  @Test
  void parseLine() {
    // Given
    var line = Collections.singletonList("PUT \"https://domain/path\" 200");
    var pattern = "(?<Method>[A-Z]+) \"(?<Path>.[^\"]+)\" (?<StatusCode>\\d+)";

    // When
    var values = sut.parseLines(line, pattern);

    // Then
    assertEquals(1, values.size());

    var value = values.get(0);
    assertEquals("PUT", value.get("Method"));
    assertEquals("https://domain/path", value.get("Path"));
    assertEquals("200", value.get("StatusCode"));
  }

  @Test
  void parseLine_withoutGroups() {
    // Given
    var line = Collections.singletonList("PUT \"https://domain/path\" 200");
    var pattern = "([A-Z]+) \"(.[^\"]+)\" (\\d+)";

    // When
    assertThrows(LogParserException.class, () -> sut.parseLines(line, pattern));
  }

  @Test
  void parseLine_unParsable() {
    // Given
    var line = Collections.singletonList("PUT \"https://domain/path\" 200");
    var pattern = "(?<Method>[1-9]+) \"(?<Path>.[^\"]+)\" (?<StatusCode>\\d+)";

    // When
    var values = sut.parseLines(line, pattern);

    // Then
    assertTrue(values.isEmpty());
  }
}
