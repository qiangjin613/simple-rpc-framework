package cn.qiangjin.dev.tech.rpc.client;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public class RequestIdSupport {

    private final static AtomicInteger nextRequestId = new AtomicInteger(0);

    public static int next() {
        return nextRequestId.getAndIncrement();
    }
}
