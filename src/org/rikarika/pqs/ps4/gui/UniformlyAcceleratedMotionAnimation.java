package org.rikarika.pqs.ps4.gui;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;

public class UniformlyAcceleratedMotionAnimation extends Animation {

  private Point from;
  private Point to;
  private Point2D.Double curPos;

  private Point2D.Double acceleration;

  private boolean stopped;
  private double timeElapsed;

  public UniformlyAcceleratedMotionAnimation(int fps, Point2D.Double acceleration, Point from,
      Point to) {
    super(fps);
    curPos = new Point2D.Double();
    curPos.setLocation(from);
    this.from = from;
    this.to = to;
    this.acceleration = acceleration;
  }

  static boolean finished(double from, double cur, double to) {
    return (from - to) * (cur - to) <= 0;
  }

  @Override
  public void update() {
    if (stopped) {
      return;
    }

    double diffX = this.acceleration.getX() * this.timeElapsed;
    double diffY = this.acceleration.getY() * this.timeElapsed;

    curPos.x += diffX;
    curPos.y += diffY;

    if (finished(from.x, curPos.x, to.x) && finished(from.y, curPos.y, to.y)) {
      stopped = true;
      curPos.setLocation(to);
    }

    timeElapsed += 1 / (double) fps;
  }

  protected Point getCurPos() {
    Point p = new Point();
    p.setLocation(curPos);
    return p;
  }

  @Override
  public void paint(Graphics g) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isStopped() {
    return stopped;
  }

}
