package org.rikarika.pqs.ps4.model;

import static org.rikarika.pqs.ps4.common.FourInARowSettings.NCOLUMN;
import static org.rikarika.pqs.ps4.common.FourInARowSettings.NROW;
import static org.rikarika.pqs.ps4.common.GameUtils.canWin;
import static org.rikarika.pqs.ps4.common.GameUtils.copyBoard;
import static org.rikarika.pqs.ps4.common.GameUtils.getRowForThisMove;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Four in a Row game logic
 * 
 * @author Jiadong Zhu
 */
public class FourInARowModel {
  private static final Logger LOGGER = Logger.getLogger("Model");

  private List<FourInARowListener> listeners;

  /**
   * Internal game state
   * 
   * @author Jiadong Zhu
   */
  private enum State {
    /** uninitialized state */
    UNINIT,
    /** game is created */
    NEW,
    /** game is started */
    STARTED,
    /** game is over */
    GAMEOVER
  }

  /* Game States */

  /**
   * Mapping from player id to chip type.
   * 
   * For convenience, player 0 use YELLOW chip and player 1 use RED
   */
  private static final Chip[] playerChip = new Chip[] {Chip.YELLOW, Chip.RED};

  /** current player id */
  private int currentPlayer;

  /** last move's row */
  private int lastRow;
  /** last move's column */
  private int lastCol;

  /** board state */
  private Chip[][] board = new Chip[NROW][NCOLUMN];

  private State state = State.UNINIT;

  private boolean[] playerReady = new boolean[] {false, false};

  public FourInARowModel() {
    listeners = new ArrayList<>();
  }

  public synchronized Chip[][] getBoard() {
    return copyBoard(board);
  }

  public Chip getPlayerChip(int player) {
    return playerChip[player];
  }

  private boolean allPlayersAreReady() {
    return playerReady[0] && playerReady[1];
  }

  private void resetPlayerReadyState() {
    Arrays.fill(playerReady, false);
  }

  public void newGame() {
    newGame((new Random()).nextInt(2));
  }

  private void startGame() {
    resetPlayerReadyState();

    state = State.STARTED;
    fireStartGameEvent(currentPlayer);
  }

  private void setReadyState(int player) {
    if (playerReady[player]) {
      return;
    }
    playerReady[player] = true;
    fireReadyListChanged();
  }

  public synchronized void newGame(int first) {
    if (state != State.UNINIT && state != State.GAMEOVER) {
      LOGGER.log(Level.WARNING, "illegal new game request. state: {0}", state);
      return;
    }

    LOGGER.log(Level.INFO, "newGame first {0}", first);

    // reset board
    for (Chip[] row : board) {
      Arrays.fill(row, null);
    }

    // set current player
    currentPlayer = first;

    // set state
    state = State.NEW;

    // reset last column and row
    lastCol = lastRow = -1;

    // trigger start event
    fireNewGameEvent(currentPlayer);
  }

  public synchronized void move(int player, int col) {
    // It's illegal to move when the game is not started or it's over.
    // Or the current player is not the given one.
    if (state != State.STARTED || player != currentPlayer) {
      LOGGER.log(Level.WARNING, "player {0} (current {1}) illegal move {2} state {3}",
          new Object[] {player, currentPlayer, col, state});
      return;
    }

    Chip chip = playerChip[currentPlayer];

    int row = getRowForThisMove(board, col);

    // update board
    board[row][col] = chip;
    LOGGER.log(Level.INFO, "this move: Player {0} row: {1} col: {2}",
        new Object[] {currentPlayer, row, col});

    resetPlayerReadyState();

    lastCol = col;
    lastRow = row;
    fireAddChipEvent(row, col, chip);

    // flip current player now (to avoid double moves)
    currentPlayer = (currentPlayer + 1) % 2;
  }

  /**
   * This method is called when the view is ready for starting the game.
   * 
   * @param player the id of the ready player
   */
  public synchronized void readyForStartGame(int player) {
    LOGGER.log(Level.INFO, "readyForStartGame acked {0}", new Object[] {player});

    setReadyState(player);

    if (!allPlayersAreReady()) {
      return;
    }

    startGame();
  }

  /**
   * This method is called when the view is ready for getting the next move from the computer
   * opponent. Judgment for the winner also happens here.
   * 
   * @param player the id of the ready player
   */
  public synchronized void readyForNextMove(int player) {
    LOGGER.log(Level.INFO, "readyForNextMove acked {0}", new Object[] {player});

    if (state != State.STARTED) {
      return;
    }

    setReadyState(player);

    if (!allPlayersAreReady()) {
      return;
    }

    // settlement
    int lastPlayer = (currentPlayer + 1) % 2;
    if (canWin(board, playerChip[lastPlayer], lastRow, lastCol)) {
      state = State.GAMEOVER;
      fireGameIsOverEvent(lastPlayer);
      LOGGER.info("Game Over.");
      return;
    }

    fireAllPlayersAreReadyForNextMoveEvent(currentPlayer);
  }

  public synchronized void addEventListener(FourInARowListener listener) {
    listeners.add(listener);
  }

  /*
   * Events
   */

  private void fireNewGameEvent(int first) {
    for (FourInARowListener listener : listeners) {
      listener.newGameCreated(first);
    }
  }

  private void fireStartGameEvent(int first) {
    for (FourInARowListener listener : listeners) {
      listener.gameIsStarted(first);
    }
  }

  private void fireAddChipEvent(int row, int col, Chip c) {
    for (FourInARowListener listener : listeners) {
      listener.chipIsAdded(row, col, currentPlayer);
    }
  }

  private void fireAllPlayersAreReadyForNextMoveEvent(int next) {
    for (FourInARowListener listener : listeners) {
      listener.allPlayersAreReadyForNextMove(next);
    }
  }

  private void fireGameIsOverEvent(int winner) {
    for (FourInARowListener listener : listeners) {
      listener.gameIsOver(winner);
    }
  }

  private void fireReadyListChanged() {
    List<Integer> readyList = new ArrayList<>();
    for (int i = 0; i < playerReady.length; i++) {
      if (playerReady[i]) {
        readyList.add(i);
      }
    }

    for (FourInARowListener listener : listeners) {
      listener.readyListChanged(readyList);
    }
  }
}
