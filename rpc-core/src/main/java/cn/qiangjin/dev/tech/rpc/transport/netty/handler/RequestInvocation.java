package cn.qiangjin.dev.tech.rpc.transport.netty.handler;

import cn.qiangjin.dev.tech.rpc.transport.RequestHandler;
import cn.qiangjin.dev.tech.rpc.transport.RequestHandlerRegistry;
import cn.qiangjin.dev.tech.rpc.transport.command.Command;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异步接收所有服务端返回的响应。
 * <p>
 * 处理逻辑比较简单，就是根据响应头中的 requestId，去在途请求 inFlightRequest 中查找对应的 ResponseFuture，
 * 设置返回值并结束这个 ResponseFuture。
 *
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
@ChannelHandler.Sharable
public class RequestInvocation extends SimpleChannelInboundHandler<Command> {

    private static final Logger logger = LoggerFactory.getLogger(RequestInvocation.class);
    private final RequestHandlerRegistry requestHandlerRegistry;

    public RequestInvocation(RequestHandlerRegistry requestHandlerRegistry) {
        this.requestHandlerRegistry = requestHandlerRegistry;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command request) throws Exception {
        // 根据请求命令的 Header 中的请求类型 type，去 requestHandlerRegistry 中查找对应的请求处理器 RequestHandler，
        // 然后调用请求处理器去处理请求，最后把结果发送给客户端。
        //
        // 这种通过“请求中的类型”，把请求分发到对应的处理类或者处理方法的设计，在 RocketMQ 和 Kafka 的源代码中都见到过，
        // 在服务端处理请求的场景中，这是一个很常用的方法。
        // 我们这里使用的也是同样的设计，不同的是，我们使用了一个命令注册机制，
        // 让这个路由分发的过程省略了大量的 if-else 或者是 switch 代码。
        // 这样做的好处是，可以很方便地扩展命令处理器，而不用修改路由分发的方法，并且代码看起来更加优雅。
        RequestHandler handler = requestHandlerRegistry.get(request.getHeader().getType());
        if (null != handler) {
            Command response = handler.handle(request);
            if (null != response) {
                ctx.writeAndFlush(response).addListener((ChannelFutureListener) channelFuture -> {
                    if (!channelFuture.isSuccess()) {
                        ctx.channel().close();
                    }
                });
            } else {
                logger.warn("Response is null!, request type: " + request.getHeader().getType());
            }
        } else {
            throw new Exception(String.format("No handler for request with type: %d!", request.getHeader().getType()));
        }
    }
}
