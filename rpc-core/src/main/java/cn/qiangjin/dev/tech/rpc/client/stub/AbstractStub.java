package cn.qiangjin.dev.tech.rpc.client.stub;

import cn.qiangjin.dev.tech.rpc.client.RequestIdSupport;
import cn.qiangjin.dev.tech.rpc.client.ServiceStub;
import cn.qiangjin.dev.tech.rpc.client.ServiceTypes;
import cn.qiangjin.dev.tech.rpc.serialize.SerializeSupport;
import cn.qiangjin.dev.tech.rpc.transport.Transport;
import cn.qiangjin.dev.tech.rpc.transport.command.Code;
import cn.qiangjin.dev.tech.rpc.transport.command.Command;
import cn.qiangjin.dev.tech.rpc.transport.command.Header;
import cn.qiangjin.dev.tech.rpc.transport.command.ResponseHeader;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 在抽象类中实现 Stub 的大部分的逻辑，让所有动态生成的桩都继承这个抽象类，这样动态生成桩的代码会更少一些。
 *
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public abstract class AbstractStub implements ServiceStub {

    protected Transport transport;

    protected byte[] invokeRemote(RpcRequest request) {
        Header header = new Header(ServiceTypes.TYPE_RPC_REQUEST, 1, RequestIdSupport.next());
        byte[] payload = SerializeSupport.serialize(request);
        Command requestCommand = new Command(header, payload);
        try {
            Command responseCommand = transport.send(requestCommand).get(5, TimeUnit.SECONDS);
            ResponseHeader responseHeader = (ResponseHeader) responseCommand.getHeader();
            if (responseHeader.getCode() == Code.SUCCESS.getCode()) {
                return responseCommand.getPayload();
            } else {
                throw new Exception(responseHeader.getError());
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        } catch (Throwable e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void setTransport(Transport transport) {
        this.transport = transport;
    }
}
