package org.rikarika.pqs.ps4.model;

import java.awt.Color;

public enum Chip {
  YELLOW(Color.YELLOW), RED(Color.RED);

  private Color c;

  private Chip(Color c) {
    this.c = c;
  }

  public Color color() {
    return c;
  }
}
