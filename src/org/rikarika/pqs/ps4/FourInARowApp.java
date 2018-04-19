package org.rikarika.pqs.ps4;

import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class FourInARowApp implements FourInARowGameEventListener {

  FourInARowGameFactory factory = new FourInARowGameFactory(this);

  JFrame frame = new JFrame("Start Window");
  JButton singleGameButton = new JButton("Single");
  JButton dualGameButton = new JButton("Dual");
  JButton joinNetGameButton = new JButton("Join LAN Game");
  JButton createNetGameButton = new JButton("Create LAN Game");
  JTextField serverTextField = new JTextField("server:port", 20);

  FourInARowGame currentGame;

  private class CommonMouseListener implements MouseListener {

    @Override
    public void mouseClicked(MouseEvent e) {
      if (e.getComponent() == singleGameButton) {
        factory.createSingleGame();
      } else if (e.getComponent() == dualGameButton) {
        factory.createDualGame();
      } else if (e.getComponent() == joinNetGameButton) {

      } else if (e.getComponent() == createNetGameButton) {

      }
    }

    @Override
    public void mousePressed(MouseEvent e) {
      // do nothing
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      // do nothing
    }

    @Override
    public void mouseEntered(MouseEvent e) {
      // do nothing

    }

    @Override
    public void mouseExited(MouseEvent e) {
      // do nothing
    }

  }

  private FourInARowApp() {
    frame.setLayout(new GridLayout(5, 1));

    MouseListener commonListener = new CommonMouseListener();

    singleGameButton.addMouseListener(commonListener);
    dualGameButton.addMouseListener(commonListener);
    joinNetGameButton.addMouseListener(commonListener);
    createNetGameButton.addMouseListener(commonListener);

    frame.add(singleGameButton);
    frame.add(dualGameButton);

    frame.add(serverTextField);

    frame.add(joinNetGameButton);
    frame.add(createNetGameButton);

    frame.pack();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    frame.setVisible(true);
  }

  @Override
  public void gameCreated(FourInARowGame game) {
    frame.setVisible(false);
    currentGame = game;
  }

  @Override
  public void gameOver() {
    frame.setVisible(true);
  }

  public static void main(String[] args) {
    new FourInARowApp();
  }


}
