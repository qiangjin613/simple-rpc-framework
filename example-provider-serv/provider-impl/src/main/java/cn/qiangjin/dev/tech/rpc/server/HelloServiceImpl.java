package cn.qiangjin.dev.tech.rpc.server;

import cn.qiangjin.dev.tech.rpc.hello.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public class HelloServiceImpl implements HelloService {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(String name) {
        logger.info("HelloServiceImpl收到: {}.", name);
        String ret = "Hello, " + name;
        logger.info("HelloServiceImpl返回: {}.", ret);
        return ret;
    }
}
