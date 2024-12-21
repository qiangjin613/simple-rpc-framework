package cn.qiangjin.dev.tech.rpc.server;

/**
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public interface ServiceProviderRegistry {

    <T> void addServiceProvider(Class<? extends T> serviceClass, T serviceProvider);
}
