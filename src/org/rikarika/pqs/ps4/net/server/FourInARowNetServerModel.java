package org.rikarika.pqs.ps4.net.server;

import static org.rikarika.pqs.ps4.common.FourInARowSettings.NPLAYERS;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class FourInARowNetServerModel {

  private ChannelGroup clients;
  private Map<Channel, Integer> clientIds = new HashMap<>();

  boolean[] playerIsReady = new boolean[] {false, false};

  int joinedPlayer = 0;

  private Logger log = Logger.getLogger("ServerModel");

  public FourInARowNetServerModel() {
    this.clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
  }

  private void broadcast(String msg) {
    log.log(Level.INFO, "broadcast {0}", new Object[] {msg});
    clients.writeAndFlush(msg + "\r\n");
  }

  void newClientAdded(ChannelHandlerContext ctx) {
    if (joinedPlayer == NPLAYERS) {
      log.warning("newClientAdded: already have 2 players");
      return;
    }

    int thisPlayerId = joinedPlayer;
    joinedPlayer++;
    clients.add(ctx.channel());
    ctx.writeAndFlush("Register " + thisPlayerId + "\r\n");
    clientIds.put(ctx.channel(), thisPlayerId);
  }

  void clientDisconnected(Channel ch) {
    log.log(Level.WARNING, "client disconnected {0}", new Object[] {clientIds.get(ch)});
  }

  void move(ChannelHandlerContext ctx, int col) {
    broadcast(String.format("Move %d,%d", clientIds.get(ctx.channel()), col));
  }

  void readyListUpdate(List<Integer> readyList) {
    String[] strArray = readyList.stream().map(String::valueOf).toArray(String[]::new);
    broadcast(String.format("Ready %s", String.join(",", strArray)));
  }

  public void playerReady(int client) {
    playerIsReady[client] = true;

    if (playerIsReady[0] && playerIsReady[1]) {
      broadcast("New " + new Random().nextInt(NPLAYERS));
    }
  }
}
