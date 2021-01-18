package server.handler.outhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class EchoOutHandler2 extends ChannelOutboundHandlerAdapter {


    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("EchoOutHandler2");
        //执行下一个outBoundHandler
        System.out.println("at first ..msg " + msg);
        msg = "hi newed in outed";
        super.write(ctx,msg,promise);
    }
}
