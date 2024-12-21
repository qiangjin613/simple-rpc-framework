package cn.qiangjin.dev.tech.rpc.transport;

import cn.qiangjin.dev.tech.rpc.transport.command.Command;

import java.util.concurrent.CompletableFuture;

/**
 * 通信接口，用于网络传输
 * <p>
 * 只需要客户端给服务端发送请求，然后服务返回响应就可以了。
 * 所以，我们的通信接口只需要提供一个发送请求方法就可以了
 *
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public interface Transport {

    /**
     * 发送请求命令
     * <p>
     * 这里面使用一个 CompletableFuture 作为返回值，使用起来就非常灵活，
     * 我们可以直接调用它的 get 方法来获取响应数据，这就相当于同步调用；
     * 也可以使用以 then 开头的一系列异步方法，指定当响应返回的时候，需要执行的操作，就等同于异步调用。
     * <p>
     * 相当于，这样一个方法既可以同步调用，也可以异步调用。
     *
     * @param request 请求命令
     * @return 返回 Future，用于获得响应结果
     */
    CompletableFuture<Command> send(Command request);
}
