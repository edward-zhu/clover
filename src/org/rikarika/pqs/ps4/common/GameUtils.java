package org.rikarika.pqs.ps4.common;

import static org.rikarika.pqs.ps4.common.FourInARowSettings.NCOLUMN;
import static org.rikarika.pqs.ps4.common.FourInARowSettings.NROW;
import java.util.Arrays;
import org.rikarika.pqs.ps4.model.Chip;

/**
 * Game logic utilities
 * 
 * @author Jiadong Zhu
 */
public class GameUtils {
  private GameUtils() {
    // pure static methods class, cannot be instantiated
  }

  /**
   * Get a copy of a board
   * 
   * @param board the board being copied
   * @return copied board
   */
  public static Chip[][] copyBoard(Chip[][] board) {
    Chip[][] copy = new Chip[NROW][];
    for (int i = 0; i < NROW; i++) {
      copy[i] = Arrays.copyOf(board[i], NCOLUMN);
    }
    return copy;
  }

  /**
   * Print a board to stdout
   * 
   * @param board board being printed
   */
  public static void printBoard(Chip[][] board) {
    for (int i = 0; i < NROW; i++) {
      for (int j = 0; j < NCOLUMN; j++) {
        char c = 'N';
        if (board[i][j] == Chip.RED) {
          c = 'R';
        } else if (board[i][j] == Chip.YELLOW) {
          c = 'Y';
        }
        System.err.printf("%c", c);
      }
      System.err.println();
    }
  }

  public static Chip getOppositeChip(Chip chip) {
    if (chip == Chip.RED) {
      return Chip.YELLOW;
    } else if (chip == Chip.YELLOW) {
      return Chip.RED;
    }

    return null;
  }

  public static int getRowForThisMove(Chip[][] board, int col) {
    // If current column is full, cannot put a new chip on this column
    if (board[0][col] != null) {
      return -1;
    }

    // for coverage test: we will never run into the i < 0 branch,
    // since the if statement above already filtered that case.
    for (int i = NROW - 1; i >= 0; i--) {
      if (board[i][col] == null) {
        return i;
      }
    }

    return -1;
  }

  private static final int[][] dirs = new int[][] {{1, 0}, {1, 1}, {0, 1}, {-1, 1}};

  public static boolean isValidPosition(int row, int col) {
    return (row >= 0) && (row < NROW) && (col >= 0) && (col < NCOLUMN);
  }

  /**
   * Check if the given last move can lead the player to win.
   * 
   * <br>
   * We search four direction:
   * 
   * <ul>
   * <li>SW to NE</li>
   * <li>W to E</li>
   * <li>NW to SE</li>
   * <li>N to S</li>
   * </ul>
   * 
   * @param board a board state
   * @param chip the chip type to check
   * @param row last move's row position
   * @param col last move's column position
   * 
   * @return whether or not this move is a winning move
   */
  public static boolean canWin(Chip[][] board, Chip chip, int row, int col) {
    if (row < 0) {
      return false;
    }
    // for each direction
    for (int[] dir : dirs) {
      // for each start point
      for (int i = 0; i < 4; i++) {
        int curRow = row - dir[0] * i;
        int curCol = col - dir[1] * i;

        boolean found = true;
        for (int j = 0; j < 4; j++) {
          if (!isValidPosition(curRow, curCol) || board[curRow][curCol] != chip) {
            found = false;
            break;
          }
          curRow += dir[0];
          curCol += dir[1];
        }
        if (found) {
          return true;
        }
      }
    }
    return false;
  }

}
