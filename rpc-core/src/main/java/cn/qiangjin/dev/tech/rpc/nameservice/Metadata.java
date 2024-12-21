package cn.qiangjin.dev.tech.rpc.nameservice;

import java.net.URI;
import java.util.HashMap;
import java.util.List;

/**
 * 用于存放注册中心的元数据。key - 服务名，value - 服务提供者 URI 列表
 *
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public class Metadata extends HashMap<String, List<URI>> {

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Metadata:\n");
        for (Entry<String, List<URI>> entry : entrySet()) {
            stringBuilder.append("\tClassname: ").append(entry.getKey()).append("\n")
                    .append("\tURIs: ").append("\n");
            for (URI uri : entry.getValue()) {
                stringBuilder.append("\t\t").append(uri).append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
