package cn.qiangjin.dev.tech.rpc.transport;

import java.io.Closeable;
import java.net.SocketAddress;
import java.util.concurrent.TimeoutException;

/**
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public interface TransportClient extends Closeable {

    Transport createTransport(SocketAddress address, long connectionTimeout) throws InterruptedException, TimeoutException;
}
