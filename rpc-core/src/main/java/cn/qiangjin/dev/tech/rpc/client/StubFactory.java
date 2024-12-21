package cn.qiangjin.dev.tech.rpc.client;

import cn.qiangjin.dev.tech.rpc.transport.Transport;

/**
 * Stub 工厂
 *
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public interface StubFactory {

    /**
     * 创建一个桩的实例
     *
     * @param transport 给服务端发请求的时候使用的
     * @param serviceClass 创建的桩的类型
     * @return 桩的实例
     * @param <T>
     */
    <T> T createStub(Transport transport, Class<T> serviceClass);
}
