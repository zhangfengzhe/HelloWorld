package day1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;

import java.nio.charset.Charset;

/**
 * Created by Administrator on 17-1-3.
 */
public class Main {

    public static void main(String[] args) {

        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // (2)
        int port = 8867;
        try {
            ServerBootstrap b = new ServerBootstrap(); // (3)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (4)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (5)
                @Override
                public void initChannel(SocketChannel ch) throws Exception {

                    ByteBuf field = Unpooled.copiedBuffer("\001", Charset.forName("UTF-8"));
                    ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,field));
                    ch.pipeline().addLast(new ServerHandler());
                }
            })
            .option(ChannelOption.SO_BACKLOG, 128)          // (6)
            .childOption(ChannelOption.SO_KEEPALIVE, true); // (7)

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync(); // (8)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            System.out.println("start server....");
            f.channel().closeFuture().sync();
            System.out.println("stop server....");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            System.out.println("exit server....");
        }

    }
}
