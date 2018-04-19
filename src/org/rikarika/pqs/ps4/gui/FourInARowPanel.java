package org.rikarika.pqs.ps4.gui;

import static org.rikarika.pqs.ps4.common.FourInARowSettings.NCOLUMN;
import static org.rikarika.pqs.ps4.common.FourInARowSettings.NROW;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.Timer;

public class FourInARowPanel extends JPanel {

  /**
   * Automatically generated serial version UID for java serialization
   */
  private static final long serialVersionUID = 5000563972128183449L;

  /**
   * State is the state of the view. It can be {@code READY}: the panel is ready for next input,
   * {@code ANIMATING}: the animation is running, and {@code STOPPED}: the game is over.
   * 
   * @author Jiadong Zhu
   */
  private enum State {
    READY, ANIMATING, STOPPED
  }

  private State state = State.READY;

  private List<Animation> animations;
  private Timer animationTimer;

  public interface AnimationEventListener {
    void animationIsStopped();
  }

  private AnimationEventListener listener;

  public FourInARowPanel() {
    super();

    this.animations = new ArrayList<>();

    this.setBackground(new Color(0, 128, 255));

    startAnimationUpdate();
  }

  public void setAnimationEventListener(AnimationEventListener listener) {
    this.listener = listener;
  }

  public void clear() {
    animations.clear();
  }

  private int chipRadius() {
    return this.getWidth() / (2 * NCOLUMN);
  }

  /**
   * Get the location and radius for given chip
   * 
   * @param row row number (negative number is valid and indicates a position outside the panel)
   * @param col column number
   * @return position of this chip
   */
  private Point getChipLocation(int row, int col) {
    int radius = chipRadius();
    int left = radius * (2 * col);
    int top = radius * (2 * row);

    return new Point(left, top);
  }

  /**
   * Return the chip position
   * 
   * @param x x position on the panel
   * @param y y position on the panel
   * @return a 2-dimension {@code int} array {@code {row, column}} of the chip, or null if the given
   *         x and y is illegal.
   */
  public int[] locateChip(int x, int y) {
    if (x < 0 || x > this.getWidth() || y < 0 || y > this.getHeight()) {
      return null;
    }

    int radius = chipRadius();
    int row = y / (radius * 2);
    int col = x / (radius * 2);

    return new int[] {row, col};
  }

  private void updateAnimation() {
    boolean allStopped = true;
    for (Animation anime : animations) {
      anime.update();
      allStopped = allStopped && anime.isStopped();
    }
    repaint();
    if (allStopped) {
      if (this.listener != null && this.state != State.READY) {
        this.listener.animationIsStopped();
      }
      this.state = State.READY;
      return;
    }
    this.state = State.ANIMATING;
  }

  private void startAnimationUpdate() {
    if (animationTimer != null) {
      return;
    }
    animationTimer = new Timer(20, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateAnimation();
      }
    });
    animationTimer.start();
  }

  public void addNewChip(int row, int col, Color color) {
    Animation anime = new ChipAnimation(50, getChipLocation(-1, col), getChipLocation(row, col),
        chipRadius(), color);
    animations.add(anime);
  }

  private void paintBoard(Graphics g) {
    g.setColor(Color.WHITE);
    for (int i = 1; i < NCOLUMN; i++) {
      Point p = this.getChipLocation(0, i);
      g.drawLine(p.x, 0, p.x, this.getHeight());
    }
    for (int i = 1; i < NROW; i++) {
      Point p = this.getChipLocation(i, 0);
      g.drawLine(0, p.y, this.getWidth(), p.y);
    }
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    paintBoard(g);
    for (Animation anime : animations) {
      anime.paint(g);
    }
  }
}
