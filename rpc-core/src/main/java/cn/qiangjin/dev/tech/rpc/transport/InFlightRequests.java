package cn.qiangjin.dev.tech.rpc.transport;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 标志在途中（暂未得到响应）的请求
 * <p>
 * 使用兜底超时的机制来保证所有情况下 ResponseFuture 都能结束。
 * <p>
 * 无论什么情况，只要超过了超时时间还没有收到响应，我们就认为这个 ResponseFuture 失败了，结束并删除它。
 *
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public class InFlightRequests implements Closeable {

    private final static long TIMEOUT_SEC = 5L;
    /**
     * 用来实现异步请求时的<b>背压机制</b>。
     * <p>
     * 每次往 inFlightRequest 中加入一个 ResponseFuture 的时候，需要先从信号量中获得一个许可，
     * 如果这时候没有许可了，就会阻塞当前这个线程，也就是发送请求的这个线程，直到有人归还了许可，才能继续发送请求。
     * 我们每结束一个在途请求，就归还一个许可，这样就可以保证在途请求的数量最多不超过 10 个请求，
     * 积压在服务端正在处理或者待处理的请求也不会超过 10 个。
     * 这样就实现了一个简单有效的背压机制。
     * <p>
     * 其实也是一种限流策略：最多只能进行 10 个请求处理
     */
    private final Semaphore semaphore = new Semaphore(10);
    private final Map<Integer, ResponseFuture> futureMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledFuture scheduledFuture;

    public InFlightRequests() {
        scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(this::removeTimeoutFutures,
                TIMEOUT_SEC, TIMEOUT_SEC, TimeUnit.SECONDS);
    }

    public void put(ResponseFuture responseFuture) throws TimeoutException, InterruptedException {
        if (semaphore.tryAcquire(TIMEOUT_SEC, TimeUnit.SECONDS)) {
            futureMap.put(responseFuture.getRequestId(), responseFuture);
        } else {
            throw new TimeoutException();
        }
    }

    public ResponseFuture remove(int requestId) {
        ResponseFuture future = futureMap.remove(requestId);
        if (null != future) {
            semaphore.release();
        }
        return future;
    }

    private void removeTimeoutFutures() {
        futureMap.entrySet().removeIf(entry -> {
            if (System.nanoTime() - entry.getValue().getTimestamp() > TIMEOUT_SEC * 1_000_000_000L) {
                semaphore.release();
                return true;
            } else {
                return false;
            }
        });
    }

    @Override
    public void close() throws IOException {
        scheduledFuture.cancel(true);
        scheduledExecutorService.shutdown();
    }
}
