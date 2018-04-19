package org.rikarika.pqs.ps4.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;

public class ChipAnimation extends UniformlyAcceleratedMotionAnimation {

  private int radius;
  private Color color;

  static final Point2D.Double G = new Point2D.Double(0, 2 * 9.8d);

  public ChipAnimation(int fps, Point from, Point to, int radius, Color color) {
    super(fps, G, from, to);
    this.radius = radius;
    this.color = color;
  }

  @Override
  public void paint(Graphics g) {
    g.setColor(this.color);
    Point p = this.getCurPos();
    g.fillOval(p.x, p.y, this.radius * 2, this.radius * 2);
  }
}
