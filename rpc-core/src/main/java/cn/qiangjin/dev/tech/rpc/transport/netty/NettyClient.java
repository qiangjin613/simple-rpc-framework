package cn.qiangjin.dev.tech.rpc.transport.netty;

import cn.qiangjin.dev.tech.rpc.transport.InFlightRequests;
import cn.qiangjin.dev.tech.rpc.transport.Transport;
import cn.qiangjin.dev.tech.rpc.transport.TransportClient;
import cn.qiangjin.dev.tech.rpc.transport.netty.codec.RequestEncoder;
import cn.qiangjin.dev.tech.rpc.transport.netty.codec.ResponseDecoder;
import cn.qiangjin.dev.tech.rpc.transport.netty.handler.ResponseInvocation;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public class NettyClient implements TransportClient {

    private EventLoopGroup ioEventGroup;
    private Bootstrap bootstrap;
    private final InFlightRequests inFlightRequests;
    private final List<Channel> channels;

    public NettyClient() {
        this.inFlightRequests = new InFlightRequests();
        this.channels = new LinkedList<>();
    }

    @Override
    public Transport createTransport(SocketAddress address, long connectionTimeout) throws InterruptedException, TimeoutException {
        return new NettyTransport(createChannel(address, connectionTimeout), inFlightRequests);
    }

    /**
     * 启动 NettyClient，返回 channel
     */
    private synchronized Channel createChannel(SocketAddress address, long connectionTimeout) throws InterruptedException, TimeoutException {
        if (address == null) {
            throw new IllegalArgumentException("address must not be null!");
        }
        if (ioEventGroup == null) {
            ioEventGroup = newIoEventGroup();
        }
        if (bootstrap == null){
            ChannelHandler channelHandlerPipeline = newChannelHandlerPipeline();
            bootstrap = newBootstrap(channelHandlerPipeline, ioEventGroup);
        }
        ChannelFuture channelFuture;
        Channel channel;
        channelFuture = bootstrap.connect(address).sync();
        if (!channelFuture.await(connectionTimeout)) {
            throw new TimeoutException();
        }
        channel = channelFuture.channel();
        if (channel == null || !channel.isActive()) {
            throw new IllegalStateException();
        }
        channels.add(channel);
        return channel;
    }

    private EventLoopGroup newIoEventGroup() {
        if (Epoll.isAvailable()) {
            return new EpollEventLoopGroup();
        } else {
            return new NioEventLoopGroup();
        }
    }

    private Bootstrap newBootstrap(ChannelHandler channelHandler, EventLoopGroup ioEventGroup) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class)
                .group(ioEventGroup)
                .handler(channelHandler)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        return bootstrap;
    }

    private ChannelHandler newChannelHandlerPipeline() {
        return new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) {
                channel.pipeline()
                        .addLast(new LoggingHandler())
                        .addLast(new ResponseDecoder())
                        .addLast(new RequestEncoder())
                        .addLast(new ResponseInvocation(inFlightRequests));
            }
        };
    }

    @Override
    public void close() throws IOException {
        for (Channel channel : channels) {
            if(null != channel) {
                channel.close();
            }
        }
        if (ioEventGroup != null) {
            ioEventGroup.shutdownGracefully();
        }
        inFlightRequests.close();
    }
}
