package org.rikarika.pqs.ps4.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.rikarika.pqs.ps4.gui.FourInARowPanel;
import org.rikarika.pqs.ps4.model.FourInARowModel;

/**
 * FourInARowGUIView provides a GUI view supports one or two local players to play the Four In A Row
 * game.
 * 
 * @author Jiadong Zhu
 *
 */
public class FourInARowGUIView extends FourInARowView
    implements FourInARowPanel.AnimationEventListener {

  private JFrame frame = new JFrame("Four In A Row");
  private FourInARowPanel gamePanel = new FourInARowPanel();

  private boolean okToMove;

  private List<Integer> players;
  private int currentPlayerIdx;

  private static final Logger LOGGER = Logger.getLogger("GUIView");

  public FourInARowGUIView(FourInARowModel model, List<Integer> players) {
    super(model);

    this.players = players;

    frame.setResizable(false);
    gamePanel.setPreferredSize(new Dimension(/* width */ 700, /* height */ 600));
    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add(gamePanel, BorderLayout.CENTER);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    gamePanel.setAnimationEventListener(this);

    gamePanel.addMouseListener(new MouseListener() {

      @Override
      public void mouseClicked(MouseEvent e) {
        mouseClickedEvent(e);
      }

      @Override
      public void mousePressed(MouseEvent e) {
        // Do nothing
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        // Do nothing
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        // Do nothing
      }

      @Override
      public void mouseExited(MouseEvent e) {
        // Do nothing
      }

    });

    frame.pack();
    frame.setVisible(true);
  }

  private void startNewGame(String prompt) {
    int gameMode =
        JOptionPane.showOptionDialog(frame, prompt, frame.getTitle(), JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE, null, new Object[] {"Single", "Dual", "Exit"}, 0);
    this.gamePanel.clear();
    switch (gameMode) {
      case 0:
        this.model.newGame();
        break;
      case 1:
        this.model.newGame();
        break;
      case 2:
        System.exit(0);
        break;
      default:
        throw new IllegalArgumentException("Invalid game mode.");
    }
  }

  private void mouseClickedEvent(MouseEvent e) {
    if (e.getButton() != MouseEvent.BUTTON1) {
      return;
    }

    int[] pos = gamePanel.locateChip(e.getX(), e.getY());

    if (pos == null) {
      return;
    }

    LOGGER.log(Level.INFO, "move {0} as player {1}",
        new Object[] {pos[1], players.get(currentPlayerIdx)});
    if (okToMove) {
      this.model.move(players.get(currentPlayerIdx), pos[1]);
    }

  }

  @Override
  public void newGameCreated(int first) {
    JOptionPane.showMessageDialog(frame,
        String.format("Game started player: %d first: %d ", players.get(currentPlayerIdx), first));

    for (int player : players) {
      this.model.readyForStartGame(player);
    }
  }

  @Override
  public void chipIsAdded(int row, int col, int player) {
    LOGGER.log(Level.INFO, "chipIsAdded player: {0} col: {1}", new Object[] {player, col});
    gamePanel.addNewChip(row, col, this.model.getPlayerChip(player).color());
  }

  @Override
  public void gameIsOver(int winner) {
    okToMove = false;
    JOptionPane.showMessageDialog(frame, "Game Over! player " + winner + " is the winner! Retry?");
    frame.setVisible(false);
  }

  @Override
  public void animationIsStopped() {
    for (int player : players) {
      this.model.readyForNextMove(player);
    }
  }

  @Override
  public void allPlayersAreReadyForNextMove(int next) {
    LOGGER.log(Level.INFO, "allPlayersAreReadyForNextMove next: {0}", next);

    okToMove = true;
    currentPlayerIdx = (currentPlayerIdx + 1) % players.size();
  }

  @Override
  public void gameIsStarted(int first) {
    LOGGER.log(Level.INFO, "gameIsStarted first: {0}", first);

    okToMove = true;
    currentPlayerIdx = players.indexOf(first);
    if (currentPlayerIdx == -1) {
      currentPlayerIdx = 0;
    }
  }

  @Override
  public void readyListChanged(List<Integer> readyList) {
    // do nothing
    LOGGER.log(Level.INFO, "readyList changed: {0}", readyList);
  }

}
