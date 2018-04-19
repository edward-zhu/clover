package org.rikarika.pqs.ps4;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.rikarika.pqs.ps4.model.FourInARowListener;
import org.rikarika.pqs.ps4.model.FourInARowModel;
import org.rikarika.pqs.ps4.net.client.FourInARowNetClient;
import org.rikarika.pqs.ps4.net.server.FourInARowNetServer;
import org.rikarika.pqs.ps4.view.FourInARowComputerPlayerView;
import org.rikarika.pqs.ps4.view.FourInARowGUIView;
import org.rikarika.pqs.ps4.view.FourInARowNetClientView;
import org.rikarika.pqs.ps4.view.FourInARowView;

public class FourInARowGameFactory {

  private FourInARowGameEventListener gameEvtListener;

  FourInARowGameFactory(FourInARowGameEventListener listener) {
    this.gameEvtListener = listener;
  }

  private class FourInARowGameImpl implements FourInARowGame, FourInARowListener {

    private FourInARowModel model;
    private Map<String, FourInARowView> views;

    private FourInARowGameImpl(FourInARowModel model, Map<String, FourInARowView> views) {
      this.model = model;
      this.views = views;

      this.model.addEventListener(this);
    }

    @Override
    public FourInARowModel getModel() {
      return model;
    }

    @Override
    public void newGameCreated(int first) {
      gameEvtListener.gameCreated(this);
    }

    @Override
    public void gameIsStarted(int first) {
      // do nothing
    }

    @Override
    public void chipIsAdded(int row, int col, int player) {
      // do nothing
    }

    @Override
    public void readyListChanged(List<Integer> readyList) {
      // do nothing
    }

    @Override
    public void allPlayersAreReadyForNextMove(int next) {
      // do nothing
    }

    @Override
    public void gameIsOver(int winner) {
      gameEvtListener.gameOver();
    }
  }

  /**
   * Create local single game (v.s. computer player)
   */
  public void createSingleGame() {
    FourInARowModel model = new FourInARowModel();
    Map<String, FourInARowView> views = new HashMap<>();
    views.put("GUI", new FourInARowGUIView(model, Arrays.asList(0)));
    views.put("COM", new FourInARowComputerPlayerView(model, 1));
    new FourInARowGameImpl(model, views).model.newGame();
  }

  public void createDualGame() {
    FourInARowModel model = new FourInARowModel();
    Map<String, FourInARowView> views = new HashMap<>();
    views.put("GUI", new FourInARowGUIView(model, Arrays.asList(0, 1)));
    new FourInARowGameImpl(model, views).model.newGame();
  }

  public void createNetGame(String server, int port) {
    FourInARowModel model = new FourInARowModel();
    Map<String, FourInARowView> views = new HashMap<>();

    FourInARowNetClient client = new FourInARowNetClient(server, port);
    
    client.startFuture().thenAcceptAsync(player -> {
      views.put("GUI", new FourInARowGUIView(model, Arrays.asList(player)));
      int opponent = (player + 1) % 2;
      views.put("NetClient", new FourInARowNetClientView(model, client, opponent));

      new FourInARowGameImpl(model, views).model.newGame();
    });
  }

  public FourInARowNetServer createNetGameWithServer(int port) {
    FourInARowNetServer server = new FourInARowNetServer();
    
    CompletableFuture<Void> future = server.startFuture();
    future.thenRunAsync(() -> createNetGame("localhost", port));

    return null;
  }



}
