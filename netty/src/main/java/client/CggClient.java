package client;

import client.handler.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class CggClient {


    private final String host;
    private final int port;


    public CggClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws InterruptedException {
        EventLoopGroup eventExecutors = null;

        try {
            //创建 引导客户端启动
            Bootstrap bootstrap = new Bootstrap();
            eventExecutors = new NioEventLoopGroup();
            bootstrap.group(eventExecutors)
            .channel(NioSocketChannel.class).remoteAddress(new InetSocketAddress(host, port))
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new ClientHandler());
                }
            });
            //调用bootstrap的connect来连接服务端
            ChannelFuture future = bootstrap.connect().sync();
            //关闭EventLoopGroup来释放资源
            future.channel().closeFuture().sync();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            eventExecutors.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) {
        try {
            new CggClient("127.0.0.1", 20001).start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
