package cn.qiangjin.dev.tech.rpc;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

/**
 * 注册中心
 *
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public interface NameService {

    /**
     * 注册服务
     *
     * @param serviceName 服务名称
     * @param uri 服务地址
     */
    void registerService(String serviceName, URI uri) throws IOException;

    /**
     * 查询服务地址
     *
     * @param serviceName 服务名称
     * @return 服务地址
     */
    URI lookupService(String serviceName) throws IOException;

    /**
     * 连接注册中心
     *
     * @param nameServiceUri 注册中心地址
     */
    void connect(URI nameServiceUri);

    /**
     * 获取所有支持的协议
     */
    Collection<String> supportedSchemes();
}
