package cn.qiangjin.dev.tech.rpc.client;

import cn.qiangjin.dev.tech.rpc.client.stub.AbstractStub;
import cn.qiangjin.dev.tech.rpc.client.stub.BaseStub;
import cn.qiangjin.dev.tech.rpc.client.stub.RpcRequest;
import cn.qiangjin.dev.tech.rpc.serialize.SerializeSupport;
import cn.qiangjin.dev.tech.rpc.transport.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 基于 JDK 动态代理的 Stub 工厂
 *
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public class JdkStubFactory implements StubFactory {

    private static final Logger logger = LoggerFactory.getLogger(JdkStubFactory.class);

    @Override
    public <T> T createStub(Transport transport, Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // 不对 Object 中方法进行代理
                        if (method.getDeclaringClass() == Object.class) {
                            return method.invoke(this, args);
                        }
                        logger.info("invoke method: {} {} {}", serviceClass.getName(), method.getName(), SerializeSupport.serialize(args[0]));
                        return SerializeSupport.parse(new BaseStub(transport).invokeRemote(
                                        new RpcRequest(
                                                serviceClass.getName(),
                                                method.getName(),
                                                SerializeSupport.serialize(args[0]))
                                )
                        );
                    }
                });
    }
}
