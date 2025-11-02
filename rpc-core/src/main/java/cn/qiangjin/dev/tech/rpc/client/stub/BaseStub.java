package cn.qiangjin.dev.tech.rpc.client.stub;

import cn.qiangjin.dev.tech.rpc.transport.Transport;

/**
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public class BaseStub extends AbstractStub {

    public BaseStub(Transport transport) {
        setTransport(transport);
    }

    @Override
    public byte[] invokeRemote(RpcRequest request) {
        return super.invokeRemote(request);
    }
}
