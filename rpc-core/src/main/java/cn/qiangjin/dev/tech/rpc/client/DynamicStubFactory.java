package cn.qiangjin.dev.tech.rpc.client;

import cn.qiangjin.dev.tech.rpc.transport.Transport;
import com.itranswarp.compiler.JavaStringCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public class DynamicStubFactory implements StubFactory {

    private static final Logger logger = LoggerFactory.getLogger(DynamicStubFactory.class);

    private final static String STUB_SOURCE_TEMPLATE =
            "package cn.qiangjin.dev.tech.rpc.client.stub;\n" +
            "import cn.qiangjin.dev.tech.rpc.serialize.SerializeSupport;\n" +
            "\n" +
            "public class %s extends AbstractStub implements %s {\n" +
            "    @Override\n" +
            "    public String %s(String arg) {\n" +
            "        return SerializeSupport.parse(\n" +
            "                invokeRemote(\n" +
            "                        new RpcRequest(\n" +
            "                                \"%s\",\n" +
            "                                \"%s\",\n" +
            "                                SerializeSupport.serialize(arg)\n" +
            "                        )\n" +
            "                )\n" +
            "        );\n" +
            "    }\n" +
            "}";

    @Override
    public <T> T createStub(Transport transport, Class<T> serviceClass) {
        // 填充模板、生成 .java 源码
        String stubSimpleName = serviceClass.getSimpleName() + "Stub";
        String classFullName = serviceClass.getName();
        String stubFullName = "cn.qiangjin.dev.tech.rpc.client.stub." + stubSimpleName;
        String methodName = serviceClass.getMethods()[0].getName();
        String source = String.format(STUB_SOURCE_TEMPLATE,
                stubSimpleName, classFullName, methodName, classFullName, methodName);
        logger.info("client 生成的代理类：\n {}", source);
        // 编译源代码 .java -> .class
        JavaStringCompiler compiler = new JavaStringCompiler();
        try {
            Map<String, byte[]> results = compiler.compile(stubSimpleName + ".java", source);
            // 加载编译好的类
            Class<?> clazz = compiler.loadClass(stubFullName, results);
            // 生成桩的实例
            ServiceStub stubInstance = (ServiceStub) clazz.newInstance();
            // 设置对应的 Transport
            stubInstance.setTransport(transport);
            return (T) stubInstance;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
