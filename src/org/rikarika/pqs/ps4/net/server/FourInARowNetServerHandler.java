package org.rikarika.pqs.ps4.net.server;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class FourInARowNetServerHandler extends SimpleChannelInboundHandler<String> {


  private FourInARowNetServerModel model;

  private ChannelHandlerContext ctx;

  public FourInARowNetServerHandler(FourInARowNetServerModel model) {
    this.model = model;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    this.ctx = ctx;
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    this.model.clientDisconnected(ctx.channel());
  }

  private void dispatch(String msg) {
    String[] grp = msg.trim().split(" ");
    String cmd = grp[0];


    String[] args = grp.length > 1 ? grp[1].split(",") : new String[] {};

    switch (cmd) {
      case "Move":
        newMoveReceived(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        break;
      case "Ready":
        readyListReceived(Arrays.stream(args).map(Integer::valueOf).collect(Collectors.toList()));
        break;
      case "Register":
        registerRequestReceived();
        break;
      case "RegisterReady":
        registerReadyReceived(Integer.parseInt(args[0]));
        break;
      default:
        throw new UnsupportedOperationException("no such method: " + cmd);
    }
  }

  private void registerReadyReceived(int client) {
    model.playerReady(client);
  }

  private void registerRequestReceived() {
    model.newClientAdded(ctx);
  }

  private void readyListReceived(List<Integer> readyList) {
    model.readyListUpdate(readyList);
  }

  private void newMoveReceived(int player, int col) {
    model.move(ctx, col);
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
    dispatch(msg);
  }

}
