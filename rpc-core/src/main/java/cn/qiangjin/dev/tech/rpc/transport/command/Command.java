package cn.qiangjin.dev.tech.rpc.transport.command;

/**
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public class Command {

    /** 命令头 */
    protected Header header;
    /** 命令中要传输的数据，是已被序列化后生成的字节数组 */
    private byte[] payload;

    public Command(Header header, byte[] payload) {
        this.header = header;
        this.payload = payload;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
}
