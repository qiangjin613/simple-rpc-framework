package cn.qiangjin.dev.tech.rpc.transport;

import cn.qiangjin.dev.tech.rpc.spi.ServiceSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public class RequestHandlerRegistry {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerRegistry.class);
    private final Map<Integer, RequestHandler> handlerMap = new HashMap<>();
    private static RequestHandlerRegistry instance = null;

    private RequestHandlerRegistry() {
        Collection<RequestHandler> requestHandlers = ServiceSupport.loadAll(RequestHandler.class);
        for (RequestHandler requestHandler : requestHandlers) {
            handlerMap.put(requestHandler.type(), requestHandler);
            logger.info("Load request handler, type: {}, class: {}.",
                    requestHandler.type(), requestHandler.getClass().getCanonicalName());
        }
    }

    public static RequestHandlerRegistry getInstance() {
        if (null == instance) {
            instance = new RequestHandlerRegistry();
        }
        return instance;
    }

    public RequestHandler get(int type) {
        return handlerMap.get(type);
    }
}
