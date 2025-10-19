package cn.qiangjin.dev.tech.rpc;

import cn.qiangjin.dev.tech.rpc.spi.ServiceSupport;
import com.sun.istack.internal.Nullable;

import java.io.Closeable;
import java.net.URI;
import java.util.Collection;

/**
 * RPC框架对外提供的服务接口（RPC 服务的接入点）
 *
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public interface RpcAccessPoint extends Closeable {

    /**
     * （Consumer）客户端获取远程服务的引用
     *
     * @param uri 远程服务地址
     * @param serviceClass 服务的接口类的Class
     * @param <T> 服务接口的类型
     * @return 远程服务引用
     */
    <T> T getRemoteService(URI uri, Class<T> serviceClass);

    /**
     * （Provider）服务端注册服务的实现实例
     *
     * @param service 实现实例
     * @param serviceClass 服务的接口类的Class
     * @param <T> 服务接口的类型
     * @return 服务地址
     */
    <T> URI addServiceProvider(T service, Class<T> serviceClass);

    /**
     * （Provider）服务端启动 RPC 框架，监听接口，开始提供远程服务。
     *
     * @return 服务实例，用于程序停止的时候安全关闭服务。
     */
    Closeable startServer() throws Exception;

    /**
     * （Consumer 及 Provider）获取注册中心的引用
     *
     * @param nameServiceUri 注册中心URI
     * @return 注册中心引用
     */
    @Nullable
    default NameService getNameService(URI nameServiceUri) {
        Collection<NameService> nameServices = ServiceSupport.loadAll(NameService.class);
        // 选择一个支持的协议返回
        for (NameService nameService : nameServices) {
            if(nameService.supportedSchemes().contains(nameServiceUri.getScheme())) {
                nameService.connect(nameServiceUri);
                return nameService;
            }
        }
        return null;
    }
}
