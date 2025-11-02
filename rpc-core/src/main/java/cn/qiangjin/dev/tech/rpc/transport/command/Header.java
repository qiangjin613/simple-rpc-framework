package cn.qiangjin.dev.tech.rpc.transport.command;

/**
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public class Header {

    /** （响应分发）唯一标识一个请求命令，在我们使用双工方式异步收发数据的时候，这个 requestId 可以用于请求和响应的配对。 */
    private int requestId;
    /**
     * 命令的版本号（或者说是传输协议的版本号，它不等同于程序的版本号）
     * <p>
     * 在设计通信协议时，让协议具备持续的升级能力，并且保持向下兼容是非常重要的。
     * 因为所有的软件，唯一不变的就是变化，由于需求一直变化，你不可能保证传输协议永远不变，
     * 一旦传输协议发生变化，为了确保使用这个传输协议的这些程序还能正常工作，或者是向下兼容，
     * 协议中必须提供一个版本号，标识收到的这条数据使用的是哪个版本的协议。
     * <p>
     * 发送方在发送命令的时候需要带上这个命令的版本号，接收方在收到命令之后必须先检查命令的版本号，
     * 如果接收方可以支持这个版本的命令就正常处理，否则就拒绝接收这个命令，返回响应告知对方：我不认识这个命令。
     * 这样才是一个完备的，可持续的升级的通信协议。
     */
    private int version;
    /** 命令的类型，主要的目的是为了能让接收命令一方来识别收到的是什么命令，以便路由到对应的处理类中去。 */
    private int type;

    public Header() {}

    public Header(int type, int version, int requestId) {
        this.type = type;
        this.version = version;
        this.requestId = requestId;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int length() {
        return Integer.BYTES + Integer.BYTES + Integer.BYTES;
    }
}
