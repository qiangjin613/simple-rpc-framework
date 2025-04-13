package cn.qiangjin.dev.tech.rpc.server;

import cn.qiangjin.dev.tech.rpc.client.ServiceTypes;
import cn.qiangjin.dev.tech.rpc.client.stub.RpcRequest;
import cn.qiangjin.dev.tech.rpc.serialize.SerializeSupport;
import cn.qiangjin.dev.tech.rpc.spi.Singleton;
import cn.qiangjin.dev.tech.rpc.transport.RequestHandler;
import cn.qiangjin.dev.tech.rpc.transport.command.Code;
import cn.qiangjin.dev.tech.rpc.transport.command.Command;
import cn.qiangjin.dev.tech.rpc.transport.command.Header;
import cn.qiangjin.dev.tech.rpc.transport.command.ResponseHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
@Singleton
public class RpcRequestHandler implements RequestHandler, ServiceProviderRegistry {

    private final static Logger logger = LoggerFactory.getLogger(RpcRequestHandler.class);
    /** key - service name, value - service provider */
    private final Map<String, Object> serviceProviders = new HashMap<>();

    /**
     * RPC 框架服务端最核心的部分
     */
    @Override
    public Command handle(Command requestCommand) {
        Header header = requestCommand.getHeader();
        // 从 payload 中反序列化 RpcRequest
        RpcRequest rpcRequest = SerializeSupport.parse(requestCommand.getPayload());
        try {
            Object serviceProvider = serviceProviders.get(rpcRequest.getInterfaceName());
            if (null != serviceProvider) {
                // 找到服务提供者，利用 Java 反射机制调用服务的对应方法
                String arg = SerializeSupport.parse(rpcRequest.getSerializedArguments());
                Method method = serviceProvider.getClass().getMethod(rpcRequest.getMethodName(), String.class);
                String result = (String) method.invoke(serviceProvider, arg);
                // 把结果封装成响应命令并返回
                return new Command(new ResponseHeader(type(), header.getVersion(), header.getRequestId()),
                        SerializeSupport.serialize(result));
            }
            // 如果没找到，返回 NO_PROVIDER 错误响应。
            logger.warn("No service Provider of {}#{}(String)!", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
            return new Command(new ResponseHeader(type(), header.getVersion(), header.getRequestId(),
                    Code.NO_PROVIDER.getCode(), "No provider!"), new byte[0]);
        } catch (Throwable t) {
            // // 发生异常，返回 UNKNOWN_ERROR 错误响应。
            logger.warn("Exception: ", t);
            return new Command(new ResponseHeader(type(), header.getVersion(), header.getRequestId(),
                    Code.UNKNOWN_ERROR.getCode(), t.getMessage()), new byte[0]);
        }
    }

    @Override
    public int type() {
        return ServiceTypes.TYPE_RPC_REQUEST;
    }

    @Override
    public <T> void addServiceProvider(Class<? extends T> serviceClass, T serviceProvider) {
        serviceProviders.put(serviceClass.getCanonicalName(), serviceProvider);
        logger.info("Add service: {}, provider: {}.",
                serviceClass.getCanonicalName(),
                serviceProvider.getClass().getCanonicalName());
    }
}
