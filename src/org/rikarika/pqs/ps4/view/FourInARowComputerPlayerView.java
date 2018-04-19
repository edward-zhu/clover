package org.rikarika.pqs.ps4.view;

import static org.rikarika.pqs.ps4.common.FourInARowSettings.NCOLUMN;
import static org.rikarika.pqs.ps4.common.GameUtils.canWin;
import static org.rikarika.pqs.ps4.common.GameUtils.copyBoard;
import static org.rikarika.pqs.ps4.common.GameUtils.getOppositeChip;
import static org.rikarika.pqs.ps4.common.GameUtils.getRowForThisMove;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.rikarika.pqs.ps4.model.Chip;
import org.rikarika.pqs.ps4.model.FourInARowModel;

/**
 * A view of a computer player.
 * 
 * One can imagine this is a virtual interface connects to a computer player, and the 'AI' player
 * will react itself when a event arrives.
 * 
 * @author Jiadong Zhu
 *
 */
public class FourInARowComputerPlayerView extends FourInARowView {

  private int playerId = -1;

  public FourInARowComputerPlayerView(FourInARowModel model, int player) {
    super(model);
    this.playerId = player;
  }


  /**
   * Give computer's next move
   * 
   * <p>
   * (algorithm from https://github.com/KenT2/python-games/blob/master/fourinarow.py)
   * 
   * @param board
   * @param chip
   * @return the probable best next move
   */
  static int getComputerNextMove(Chip[][] board, Chip chip, int lookAhead) {
    double[] goodnesses = getComputerPotentialMoves(board, chip, lookAhead);

    double bestMoveGoodness = Double.NEGATIVE_INFINITY;

    List<Integer> goodMoves = new ArrayList<>();

    for (int i = 0; i < NCOLUMN; i++) {
      if (goodnesses[i] > bestMoveGoodness) {
        bestMoveGoodness = goodnesses[i];
      }
    }

    for (int i = 0; i < NCOLUMN; i++) {
      if (goodnesses[i] == bestMoveGoodness && getRowForThisMove(board, i) >= 0) {
        goodMoves.add(i);
      }
    }

    return goodMoves.get(new Random().nextInt(goodMoves.size()));
  }

  /**
   * Calculate the "goodnesses" of next move for a board setting.
   * 
   * @param board current board setting
   * @param chip player's chip type
   * @param lookAhead look-ahead steps
   * @return a NCOLUMN-length array of the goodnesses of choosing each column for the next step.
   */
  private static double[] getComputerPotentialMoves(Chip[][] board, Chip chip, int lookAhead) {
    double[] goodnesses = new double[NCOLUMN];

    if (lookAhead == 0) {
      return goodnesses;
    }

    Chip humanChip = getOppositeChip(chip);

    // this step, try every column
    for (int thisMove = 0; thisMove < NCOLUMN; thisMove++) {
      int row = getRowForThisMove(board, thisMove);
      if (row < 0) {
        // this row is full
        goodnesses[thisMove] = 0;
        continue;
      }

      Chip[][] nextBoard = copyBoard(board);
      nextBoard[row][thisMove] = chip;

      if (canWin(nextBoard, chip, row, thisMove)) {
        // if we can win this game, set the goodness to 1
        goodnesses[thisMove] = 1;
        continue;
      }
      // else continue search for human's move
      for (int humanMove = 0; humanMove < NCOLUMN; humanMove++) {
        int humanRow = getRowForThisMove(nextBoard, humanMove);
        if (humanRow < 0) {
          continue;
        }

        Chip[][] nextBoard2 = copyBoard(nextBoard);
        nextBoard2[humanRow][humanMove] = humanChip;
        if (canWin(nextBoard2, humanChip, humanRow, humanMove)) {
          // human can win in this branch
          goodnesses[thisMove] = -1;
          continue;
        }

        // continue search next level
        double[] nextGoodness = getComputerPotentialMoves(nextBoard2, chip, lookAhead - 1);
        // normalize twice once for next level's our move, and for this level's human's move
        goodnesses[thisMove] += Arrays.stream(nextGoodness).sum() / (NCOLUMN * NCOLUMN);
      }
    }

    return goodnesses;
  }

  private void move() {
    int col = getComputerNextMove(this.model.getBoard(), this.model.getPlayerChip(playerId), 1);
    this.model.move(playerId, col);

  }

  @Override
  public void newGameCreated(int first) {
    this.model.readyForStartGame(playerId);
  }

  @Override
  public void chipIsAdded(int row, int col, int player) {
    System.out.printf("com: chipIsAdded\n");
    this.model.readyForNextMove(playerId);
  }

  @Override
  public void allPlayersAreReadyForNextMove(int next) {
    System.out.printf("com: next move first %d self %d\n", next, playerId);
    if (next == playerId) {
      move();
    }
  }

  @Override
  public void gameIsOver(int winner) {
    // do nothing
  }

  @Override
  public void gameIsStarted(int first) {
    System.out.printf("com: new game first %d self %d\n", first, playerId);
    if (first == playerId) {
      move();
    }
  }

  @Override
  public void readyListChanged(List<Integer> readyList) {
    // do nothing
  }

}
