package org.rikarika.pqs.ps4.gui;

import java.awt.Graphics;

public abstract class Animation {

  protected int fps;

  public Animation(int fps) {
    this.fps = fps;
  }

  public abstract void update();

  public abstract void paint(Graphics g);

  public abstract boolean isStopped();
}
