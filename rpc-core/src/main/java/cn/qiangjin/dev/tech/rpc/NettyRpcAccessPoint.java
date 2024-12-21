package cn.qiangjin.dev.tech.rpc;

import cn.qiangjin.dev.tech.rpc.client.StubFactory;
import cn.qiangjin.dev.tech.rpc.server.ServiceProviderRegistry;
import cn.qiangjin.dev.tech.rpc.spi.ServiceSupport;
import cn.qiangjin.dev.tech.rpc.transport.RequestHandlerRegistry;
import cn.qiangjin.dev.tech.rpc.transport.Transport;
import cn.qiangjin.dev.tech.rpc.transport.TransportClient;
import cn.qiangjin.dev.tech.rpc.transport.TransportServer;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

/**
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public class NettyRpcAccessPoint implements RpcAccessPoint {

    private final String host = "localhost";
    private final int port = 9999;
    private final URI uri = URI.create("rpc://" + host + ":" + port);

    private TransportServer server = null;
    private TransportClient client = ServiceSupport.load(TransportClient.class);

    private final Map<URI, Transport> clientMap = new ConcurrentHashMap<>();
    private final StubFactory stubFactory = ServiceSupport.load(StubFactory.class);
    private final ServiceProviderRegistry serviceProviderRegistry = ServiceSupport.load(ServiceProviderRegistry.class);

    @Override
    public <T> T getRemoteService(URI uri, Class<T> serviceClass) {
        Transport transport = clientMap.computeIfAbsent(uri, this::createTransport);
        return stubFactory.createStub(transport, serviceClass);
    }

    private Transport createTransport(URI uri) {
        try {
            return client.createTransport(new InetSocketAddress(uri.getHost(), uri.getPort()),30000L);
        } catch (InterruptedException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized <T> URI addServiceProvider(T service, Class<T> serviceClass) {
        serviceProviderRegistry.addServiceProvider(serviceClass, service);
        return uri;
    }

    @Override
    public synchronized Closeable startServer() throws Exception {
        if (null == server) {
            server = ServiceSupport.load(TransportServer.class);
            server.start(RequestHandlerRegistry.getInstance(), port);
        }
        // 启动 server 时提供一个 Closeable 对象
        return () -> {
            if (null != server) {
                server.stop();
            }
        };
    }

    @Override
    public void close() throws IOException {
        if (null != server) {
            server.stop();
        }
        client.close();
    }
}
