package cn.qiangjin.dev.tech.rpc.transport;

import cn.qiangjin.dev.tech.rpc.transport.command.Command;

/**
 * 请求处理器
 *
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public interface RequestHandler {

    /**
     * 处理请求
     *
     * @param requestCommand 请求命令
     * @return 响应命令
     */
    Command handle(Command requestCommand);

    /**
     * 获取支持的请求类型
     *
     * @return 支持的请求类型
     */
    int type();
}
