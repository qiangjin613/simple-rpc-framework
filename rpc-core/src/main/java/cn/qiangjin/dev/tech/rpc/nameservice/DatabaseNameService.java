package cn.qiangjin.dev.tech.rpc.nameservice;

import cn.qiangjin.dev.tech.rpc.NameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;

/**
 * 基于数据库的注册中心
 *
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public class DatabaseNameService implements NameService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseNameService.class);
    private static final Collection<String> SCHEMES = Collections.singleton("db");

    @Override
    public Collection<String> supportedSchemes() {
        return SCHEMES;
    }

    @Override
    public void connect(URI nameServiceUri) {

    }

    @Override
    public void registerService(String serviceName, URI uri) throws IOException {
        // 这里加锁，就要添加 数据库的锁/分布式锁
    }

    @Override
    public URI lookupService(String serviceName) throws IOException {
        return null;
    }
}
