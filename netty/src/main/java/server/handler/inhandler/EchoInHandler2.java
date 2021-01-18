package server.handler.inhandler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

public class EchoInHandler2 extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("EchoInHandler2");
        ByteBuf byteBuf = (ByteBuf) msg;
        //声明字节数组
        byte[] bytes = new byte[byteBuf.readableBytes()];
        //读取msg数据到 字节数组中
        byteBuf.readBytes(bytes);
        //将字节数组转换为字符串，字节流-》字符流需要指定码表
        String body = new String(bytes, "utf-8");
        System.out.println("接收客户端数据 " + body);
        //向客户端写数据
        System.out.println("server 向客户端发送数据 ");
        String currentTime = new Date(System.currentTimeMillis()).toString();
        ByteBuf response = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.write(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //刷新后将数据发送到SocketChannel
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
