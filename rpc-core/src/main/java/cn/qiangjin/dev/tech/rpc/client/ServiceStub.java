package cn.qiangjin.dev.tech.rpc.client;

import cn.qiangjin.dev.tech.rpc.transport.Transport;

/**
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public interface ServiceStub {

    void setTransport(Transport transport);
}
