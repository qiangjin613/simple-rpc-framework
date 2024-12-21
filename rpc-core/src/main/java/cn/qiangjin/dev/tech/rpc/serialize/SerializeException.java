package cn.qiangjin.dev.tech.rpc.serialize;

/**
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public class SerializeException extends RuntimeException {

    public SerializeException(String message) {
        super(message);
    }

    public SerializeException(Throwable cause) {
        super(cause);
    }
}
