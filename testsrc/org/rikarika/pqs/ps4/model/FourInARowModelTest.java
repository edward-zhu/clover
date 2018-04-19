package org.rikarika.pqs.ps4.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.rikarika.pqs.ps4.common.GameUtils.canWin;
import org.junit.Test;

public class FourInARowModelTest {

  Chip[][] genBoard(String[] b) {
    int nRow = b.length;
    int nColumn = b[0].length();
    Chip[][] board = new Chip[nRow][nColumn];

    for (int i = 0; i < nRow; i++) {
      for (int j = 0; j < nColumn; j++) {
        Chip c = null;
        switch (b[i].charAt(j)) {
          case 'Y':
            c = Chip.YELLOW;
            break;
          case 'R':
            c = Chip.RED;
            break;
        }
        board[i][j] = c;
      }
    }

    return board;
  }

  @Test
  public void testCanWin() {
    String[] canWinBoard = {
        "NNNNNNN",
        "NNNNNNN",
        "NNNNNYN",
        "NNNRRRN",
        "NNNYYYY",
        "NNNRYRY"
    };

    assertTrue(canWin(genBoard(canWinBoard), Chip.YELLOW, 4, 6));

    String[] cannotWinBoard = {
        "NNNNNNN",
        "NNNNNNN",
        "NNNNNYN",
        "NNNRRRN",
        "NNNYYYN",
        "NNNRYRY"
    };

    assertFalse(canWin(genBoard(cannotWinBoard), Chip.YELLOW, 4, 5));

    String[] canWinBoard1 = {
        "NNNNNNN",
        "NNNNNNN",
        "NNNNRNN",
        "NNNRYNN",
        "NNRYYNN",
        "RRYYYRN"
    };

    assertTrue(canWin(genBoard(canWinBoard1), Chip.RED, 2, 4));


  }

  /*
  @Test
  public void testComputerMove() {
    String[] b = {
        "NNNNNNN",
        "NNNNNNN",
        "NNNNNYN",
        "NNNRRRN",
        "NNNYYYN",
        "NNNRYRY"
    };

    // the Computer must put a chip at column 6 to win this game
    assertEquals(6, FourInARowModel.getComputerNextMove(genBoard(b), Chip.YELLOW, 1));

    // the Computer must put a chip at column 6 to prevent human win this game
    assertEquals(6, FourInARowModel.getComputerNextMove(genBoard(b), Chip.RED, 1));

    String[] b1 = {
        "NNNNNNN",
        "NNNNNNN",
        "NNNNNNN",
        "NNNRYYN",
        "NNRRYYN",
        "NRRYYRN"
    };
    assertEquals(4, FourInARowModel.getComputerNextMove(genBoard(b1), Chip.RED, 1));

    String[] boardWithFullColumn = {
        "NNNNNYN",
        "NNNNRRN",
        "NNNNRYN",
        "NNNRRYN",
        "NNYRYYN",
        "NNRYYRN"
    };
    assertEquals(4,
        FourInARowModel.getComputerNextMove(genBoard(boardWithFullColumn), Chip.RED, 1));

    String[] bottom = {
        "NNNNNNN",
        "NNNNNNN",
        "NNNNNNN",
        "NYRNNNN",
        "NYRRNNN",
        "RRYYYNN"
    };

    assertEquals(5, FourInARowModel.getComputerNextMove(genBoard(bottom), Chip.YELLOW, 1));

    String[] top = {
        "NNNNNNN",
        "NNRNNNN",
        "NNRNNNN",
        "NYRNNNN",
        "NYYRNNN",
        "RRYYYRN"
    };

    assertEquals(2, FourInARowModel.getComputerNextMove(genBoard(top), Chip.RED, 1));
  }
  */


}
