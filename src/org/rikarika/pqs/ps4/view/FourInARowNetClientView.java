package org.rikarika.pqs.ps4.view;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.rikarika.pqs.ps4.model.FourInARowModel;
import org.rikarika.pqs.ps4.net.client.FourInARowNetClient;
import org.rikarika.pqs.ps4.net.client.FourInARowNetEventListener;


public class FourInARowNetClientView extends FourInARowView implements FourInARowNetEventListener {

  private FourInARowNetClient client;

  private enum State {
    READY, STARTED, GAMEOVER
  }

  private int playerId = -1; // remote player's id
  private State state = State.READY;

  private static final Logger LOGGER = Logger.getLogger("ClientView");

  public FourInARowNetClientView(FourInARowModel model, FourInARowNetClient client, int player) {
    super(model);
    this.client = client;
    this.playerId = player;

    client.setEventListener(this);
  }

  @Override
  public void newGameCreated(int first) {
    state = State.READY;
  }

  @Override
  public void gameIsStarted(int first) {
    state = State.STARTED;
  }

  @Override
  public void chipIsAdded(int row, int col, int player) {
    // client send the local new move to server
    if (player != this.playerId) {
      client.sendNewLocalMove(player, col);
    }
  }

  @Override
  public void allPlayersAreReadyForNextMove(int next) {
    // do nothing
  }

  @Override
  public void gameIsOver(int winner) {
    // do nothing
    state = State.GAMEOVER;
  }

  @Override
  public void readyListChanged(List<Integer> readyList) {
    // If local GUI is ready, it will bcast this information to
    // other clients (through server)

    LOGGER.log(Level.INFO, "state {0} ready list {1}", new Object[] {state, readyList});

    if (state == State.READY) {
      // send new list to server
      client.sendReadyList(readyList);
    }
  }

  @Override
  public void readyListFromServer(List<Integer> remoteReadyList) {
    LOGGER.log(Level.INFO, "readyListFromServer {0} State = {1}",
        new Object[] {remoteReadyList, state});

    for (int player : remoteReadyList) {
      if (state == State.READY) {
        model.readyForStartGame(player);
      }
    }
  }

  @Override
  public void newGameFromServer(int first) {
    this.model.newGame(first);
  }

  @Override
  public void moveFromServer(int player, int col) {
    if (player == this.playerId) {
      model.move(player, col);
    }

    model.readyForNextMove(this.playerId);
  }

}
