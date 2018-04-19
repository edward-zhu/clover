package org.rikarika.pqs.ps4.net.server;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class FourInARowNetServer {
  public void start() throws InterruptedException {
    start(null);
  }

  private void start(CompletableFuture<Void> future) throws InterruptedException {
    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    ServerBootstrap b = new ServerBootstrap();

    FourInARowNetServerModel model = new FourInARowNetServerModel();

    try {
      b.group(bossGroup)
          .channel(NioServerSocketChannel.class)
          .handler(new LoggingHandler(LogLevel.INFO))
          .childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
              ChannelPipeline pipeline = ch.pipeline();
              pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
              pipeline.addLast(new StringDecoder());
              pipeline.addLast(new StringEncoder());

              pipeline.addLast(new FourInARowNetServerHandler(model));
              
              if (future != null) {
                future.complete(null);
              }
            }
          });

      b.bind(8484).sync().channel().closeFuture().sync();
    } finally {
      bossGroup.shutdownGracefully();
    }
  }

  public CompletableFuture<Void> startFuture() {
    CompletableFuture<Void> f = new CompletableFuture<>();
    
    ExecutorService executor = Executors.newSingleThreadExecutor();
    
    executor.execute(() -> {
      try {
        start(f);
      } catch (InterruptedException ignored) {
        Thread.currentThread().interrupt();
      }
    });
    
    return f;
  }

  public static void main(String[] args) throws Exception {
    new FourInARowNetServer().start();
  }
}
