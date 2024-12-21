package cn.qiangjin.dev.tech.rpc.transport.netty;

import cn.qiangjin.dev.tech.rpc.transport.InFlightRequests;
import cn.qiangjin.dev.tech.rpc.transport.ResponseFuture;
import cn.qiangjin.dev.tech.rpc.transport.Transport;
import cn.qiangjin.dev.tech.rpc.transport.command.Command;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

import java.util.concurrent.CompletableFuture;

/**
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public class NettyTransport implements Transport {

    private final InFlightRequests inFlightRequests;
    private final Channel channel;

    public NettyTransport(Channel channel, InFlightRequests inFlightRequests) {
        this.channel = channel;
        this.inFlightRequests = inFlightRequests;
    }

    /**
     * 需要注意的一点是，已经发出去的请求，有可能会因为网络连接断开或者对方进程崩溃等各种异常情况，永远都收不到响应。
     * 那为了确保这些孤儿 ResponseFuture 不会在内存中越积越多，我们必须要捕获所有的异常情况，结束对应的 ResponseFuture。
     * 所以，我们在代码中，两个地方都做了异常处理，分别应对发送失败和发送异常两种情况。
     */
    @Override
    public CompletableFuture<Command> send(Command request) {
        CompletableFuture<Command> completableFuture = new CompletableFuture<>();
        try {
            // 将在途请求放到 inFlightRequests 中
            // 即使是我们对所有能捕获的异常都做了处理，也不能保证所有 ResponseFuture 都能正常或者异常结束，
            // 比如说，编写对端程序的程序员写的代码有问题，收到了请求就是没给我们返回响应，
            // 为了应对这种情况，还必须有一个兜底超时的机制来保证所有情况下 ResponseFuture 都能结束，
            // 无论什么情况，只要超过了超时时间还没有收到响应，我们就认为这个 ResponseFuture 失败了，结束并删除它。
            inFlightRequests.put(new ResponseFuture(request.getHeader().getRequestId(), completableFuture));
            channel.writeAndFlush(request).addListener((ChannelFutureListener) channelFuture -> {
                // 处理发送失败的情况
                if (!channelFuture.isSuccess()) {
                    completableFuture.completeExceptionally(channelFuture.cause());
                    channel.close();
                }
            });
        }
        // 处理发送异常
        catch (Throwable throwable) {
            inFlightRequests.remove(request.getHeader().getRequestId());
            completableFuture.completeExceptionally(throwable);
        }
        return completableFuture;
    }
}
