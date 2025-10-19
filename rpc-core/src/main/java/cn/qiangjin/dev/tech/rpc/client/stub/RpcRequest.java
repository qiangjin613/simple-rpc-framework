package cn.qiangjin.dev.tech.rpc.client.stub;

/**
 * 表示远程方法
 *
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public class RpcRequest {

    /** 接口 */
    private final String interfaceName;
    /** 方法 */
    private final String methodName;
    /** 形参 */
    private final byte [] serializedArguments;

    public RpcRequest(String interfaceName, String methodName, byte[] serializedArguments) {
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.serializedArguments = serializedArguments;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public byte[] getSerializedArguments() {
        return serializedArguments;
    }
}
