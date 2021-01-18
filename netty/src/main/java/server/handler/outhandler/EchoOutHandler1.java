package server.handler.outhandler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.util.Date;

public class EchoOutHandler1 extends ChannelOutboundHandlerAdapter {


    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        //向client发送消息
        System.out.println("EchoOutHandler1");
        System.out.println(msg);
        String currentTime = new Date(System.currentTimeMillis()).toString();
        ByteBuf response = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.write(response);
        ctx.flush();
    }
}
