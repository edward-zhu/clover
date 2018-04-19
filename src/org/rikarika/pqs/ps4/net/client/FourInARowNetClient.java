package org.rikarika.pqs.ps4.net.client;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class FourInARowNetClient {
  private static final Logger LOGGER = Logger.getLogger("Client");

  private FourInARowNetEventListener listener;

  private final String host;
  private final int port;

  private EventLoopGroup evtLoopGroup;
  private ChannelHandlerContext clientCxt;

  private CompletableFuture<Integer> future;

  public FourInARowNetClient(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public void setEventListener(FourInARowNetEventListener listener) {
    this.listener = listener;
  }

  public void sendNewLocalMove(int player, int col) {
    send(String.format("Move %d,%d", player, col));
  }

  public void sendNewGame(int first) {
    send(String.format("New %d", first));
  }

  public void sendReadyList(List<Integer> readyList) {
    String[] strArray = readyList.stream().map(String::valueOf).toArray(String[]::new);
    send(String.format("Ready %s", String.join(",", strArray)));
  }

  public void registerClient() {
    send("Register");
  }

  private void send(String msg) {
    LOGGER.log(Level.INFO, "send: {0}", msg);
    final String line = msg + "\r\n";

    assert (evtLoopGroup != null);

    if (clientCxt.executor().inEventLoop()) {
      clientCxt.writeAndFlush(line);
      return;
    }

    clientCxt.executor().execute(() -> clientCxt.writeAndFlush(line));
  }

  private class Handler extends SimpleChannelInboundHandler<String> {

    private void newMoveReceived(int player, int col) {
      listener.moveFromServer(player, col);
    }

    private void readyListReceived(List<Integer> readyList) {
      listener.readyListFromServer(readyList);
    }

    private void newGameReceived(int first) {
      listener.newGameFromServer(first);
    }

    private void dispatch(String msg) {
      LOGGER.log(Level.INFO, "dispatch: {0}", msg);

      String[] grp = msg.trim().split(" ");
      String cmd = grp[0];
      String[] args = grp[1].split(",");

      switch (cmd) {
        case "Move":
          newMoveReceived(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
          break;
        case "New":
          newGameReceived(Integer.parseInt(args[0]));
          break;
        case "Ready":
          readyListReceived(Arrays.stream(args).map(Integer::valueOf).collect(Collectors.toList()));
          break;
        case "Register":
          registerResult(Integer.parseInt(args[0]));
          break;
        default:
          throw new UnsupportedOperationException("no such method: " + cmd);
      }
    }

    private void registerResult(int id) {
      future.complete(id);
      send("RegisterReady " + id);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
      dispatch(msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
      clientCxt = ctx;
      registerClient();
    }
  }

  public void start() throws InterruptedException {
    start(null);
  }

  public void start(CompletableFuture<Integer> future) throws InterruptedException {
    this.future = future;
    EventLoopGroup group = new NioEventLoopGroup();
    evtLoopGroup = group;
    try {
      Bootstrap b = new Bootstrap();
      b
          .group(group)
          .channel(NioSocketChannel.class)
          .option(ChannelOption.TCP_NODELAY, true) // ensure that message is sent out immediately
          .handler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
              ChannelPipeline p = ch.pipeline();
              p.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
              p.addLast(new StringDecoder());
              p.addLast(new StringEncoder());
              p.addLast(new Handler());
            }

          });
      ChannelFuture f = b.connect(host, port).sync();

      f.channel().closeFuture().sync();
    } finally {
      group.shutdownGracefully().sync();
    }
  }

  public CompletableFuture<Integer> startFuture() {
    CompletableFuture<Integer> f = new CompletableFuture<>();

    ExecutorService executor = Executors.newSingleThreadExecutor();
    executor.execute(() -> {
      try {
        start(f);
      } catch (InterruptedException e) {
        LOGGER.warning("interrupted!!");
        Thread.currentThread().interrupt();
      }
    });

    return f;
  }
}
