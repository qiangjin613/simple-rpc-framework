package cn.qiangjin.dev.tech.rpc.client;

import cn.qiangjin.dev.tech.rpc.client.stub.AbstractStub;
import cn.qiangjin.dev.tech.rpc.client.stub.BaseStub;
import cn.qiangjin.dev.tech.rpc.client.stub.RpcRequest;
import cn.qiangjin.dev.tech.rpc.serialize.SerializeSupport;
import cn.qiangjin.dev.tech.rpc.transport.Transport;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public class CGlibStubFactory implements StubFactory {

    private final static Logger logger = LoggerFactory.getLogger(CGlibStubFactory.class);

    @Override
    public <T> T createStub(Transport transport, Class<T> serviceClass) {
        Enhancer enhancer = new Enhancer();
        enhancer.setInterfaces(new Class[]{serviceClass, ServiceStub.class});
        enhancer.setSuperclass(AbstractStub.class);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                // 如果方法不是 serviceClass 类中声明的，直接调用默认行为
                // 不对 Object 中方法进行代理
                if (method.getDeclaringClass() == Object.class) {
                    return method.invoke(this, objects);
                }
                logger.info("invoke method: {} {} {}",serviceClass.getName(), method.getName(), SerializeSupport.serialize(objects[0]));
                return SerializeSupport.parse(
                        new BaseStub(transport).invokeRemote(
                                new RpcRequest(
                                        serviceClass.getName(),
                                        method.getName(),
                                        SerializeSupport.serialize(objects[0]))
                        )
                );
            }
        });
        return (T) enhancer.create();
    }
}
