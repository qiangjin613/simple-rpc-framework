package cn.qiangjin.dev.tech.rpc.transport;

import cn.qiangjin.dev.tech.rpc.transport.command.Command;

import java.util.concurrent.CompletableFuture;

/**
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public class ResponseFuture {

    private final int requestId;
    private final CompletableFuture<Command> future;
    private final long timestamp;

    public ResponseFuture(int requestId, CompletableFuture<Command> future) {
        this.requestId = requestId;
        this.future = future;
        timestamp = System.nanoTime();
    }

    public int getRequestId() {
        return requestId;
    }

    public CompletableFuture<Command> getFuture() {
        return future;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
