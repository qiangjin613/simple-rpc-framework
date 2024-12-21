package cn.qiangjin.dev.tech.rpc.server;

import cn.qiangjin.dev.tech.rpc.NameService;
import cn.qiangjin.dev.tech.rpc.RpcAccessPoint;
import cn.qiangjin.dev.tech.rpc.hello.HelloService;
import cn.qiangjin.dev.tech.rpc.spi.ServiceSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.net.URI;

/**
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public class Server {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws Exception {
        String serviceName = HelloService.class.getCanonicalName();

        File tmpDirFile = new File(System.getProperty("java.io.tmpdir"));
        File file = new File(tmpDirFile, "simple_rpc_name_service.data");
        HelloServiceImpl helloService = new HelloServiceImpl();

        logger.info("创建并启动 RpcAccessPoint...");
        try (
                RpcAccessPoint rpcAccessPoint = ServiceSupport.load(RpcAccessPoint.class);
                Closeable ignored = rpcAccessPoint.startServer()
        ){
            NameService nameService = rpcAccessPoint.getNameService(file.toURI());
            assert nameService != null;
            logger.info("向 RpcAccessPoint 注册 {} 服务...", serviceName);
            URI uri = rpcAccessPoint.addServiceProvider(helloService, HelloService.class);
            logger.info("服务名: {}, 向 NameService 注册...", serviceName);
            nameService.registerService(serviceName, uri);
            logger.info("开始提供服务，按任何键退出.");
            System.in.read();
            logger.info("Bye!");
        }
    }
}
