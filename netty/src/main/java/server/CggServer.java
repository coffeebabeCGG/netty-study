package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import server.handler.inhandler.EchoInHandler1;
import server.handler.inhandler.EchoInHandler2;
import server.handler.outhandler.EchoOutHandler1;
import server.handler.outhandler.EchoOutHandler2;

/**
 * 模拟服务端
 * 当有一个请求 或者 连接 链接上时做处理
 */
public class CggServer {

    private final int port;


    public CggServer(int port) {
        this.port = port;
    }

    public void start() {
        EventLoopGroup eventExecutors = null;
        EventLoopGroup work = null;
        try {
            //server
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //连接池处理数据
            eventExecutors = new NioEventLoopGroup();
            work = new NioEventLoopGroup();
            //server设置连接池
            serverBootstrap.group(eventExecutors, work)
            //server指定通道类型为
            .channel(NioServerSocketChannel.class)
            .localAddress("127.0.0.1", port)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channel) {
                    //注册两个Inbound,执行顺序正序
//                    channel.pipeline().addLast(new EchoInHandler1());
                    channel.pipeline().addLast(new EchoInHandler2());
                    //注册两个Outbound,执行顺序逆序
                    channel.pipeline().addLast(new EchoOutHandler1());
//                    channel.pipeline().addLast(new EchoOutHandler2());
                }
            });
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            System.out.println("开始监听 端口为: " + channelFuture.channel().localAddress());
            channelFuture.channel().closeFuture().sync();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                eventExecutors.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        new CggServer(20001).start();
    }
}
