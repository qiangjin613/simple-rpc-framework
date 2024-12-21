package cn.qiangjin.dev.tech.rpc.transport.netty.handler;

import cn.qiangjin.dev.tech.rpc.transport.InFlightRequests;
import cn.qiangjin.dev.tech.rpc.transport.ResponseFuture;
import cn.qiangjin.dev.tech.rpc.transport.command.Command;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public class ResponseInvocation extends SimpleChannelInboundHandler<Command> {

    private static final Logger logger = LoggerFactory.getLogger(ResponseInvocation.class);
    private final InFlightRequests inFlightRequests;

    public ResponseInvocation(InFlightRequests inFlightRequests) {
        this.inFlightRequests = inFlightRequests;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command response) {
        ResponseFuture future = inFlightRequests.remove(response.getHeader().getRequestId());
        if(null != future) {
            future.getFuture().complete(response);
        } else {
            logger.warn("Drop response: {}", response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("Exception: ", cause);
        super.exceptionCaught(ctx, cause);
        Channel channel = ctx.channel();
        if(channel.isActive()) {
            ctx.close();
        }
    }
}
