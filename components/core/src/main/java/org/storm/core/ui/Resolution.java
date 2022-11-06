package org.storm.core.ui;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a screen resolution
 */
@Getter
@AllArgsConstructor
public class Resolution {
  private final double width;
  private final double height;
}
