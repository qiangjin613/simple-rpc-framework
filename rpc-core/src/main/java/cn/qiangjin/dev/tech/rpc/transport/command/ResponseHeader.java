package cn.qiangjin.dev.tech.rpc.transport.command;

import java.nio.charset.StandardCharsets;

/**
 * 返回的响应 Header
 *
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public class ResponseHeader extends Header {

    /** 响应状态，0 代表成功，其他值分别代表各种错误，这个设计和 HTTP 协议中的 StatueCode 是一样的。 */
    private int code;
    /** 响应的错误信息 */
    private String error;

    public ResponseHeader(int type, int version, int requestId,  Throwable throwable) {
        this(type, version, requestId, Code.UNKNOWN_ERROR.getCode(), throwable.getMessage());
    }

    public ResponseHeader(int type, int version, int requestId) {
        this(type, version, requestId, Code.SUCCESS.getCode(), null);
    }

    public ResponseHeader(int type, int version, int requestId , int code, String error) {
        super(type, version, requestId);
        this.code = code;
        this.error = error;
    }

    @Override
    public int length() {
        return Integer.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES +
                Integer.BYTES +
                (error == null ? 0 : error.getBytes(StandardCharsets.UTF_8).length);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
