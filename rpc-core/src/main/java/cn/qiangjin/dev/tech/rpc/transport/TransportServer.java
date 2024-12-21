package cn.qiangjin.dev.tech.rpc.transport;

/**
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public interface TransportServer {

    void start(RequestHandlerRegistry requestHandlerRegistry, int port) throws Exception;

    void stop();
}
