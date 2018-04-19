package org.rikarika.pqs.ps4.net.client;

import java.util.List;

public interface FourInARowNetEventListener {
  public void newGameFromServer(int first);

  public void readyListFromServer(List<Integer> readyList);

  // public void startGameFromServer(int first);

  public void moveFromServer(int player, int col);
}
