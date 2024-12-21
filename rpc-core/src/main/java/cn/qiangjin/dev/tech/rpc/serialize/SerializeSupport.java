package cn.qiangjin.dev.tech.rpc.serialize;

import cn.qiangjin.dev.tech.rpc.spi.ServiceSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 支持任何对象类型序列化的通用静态类
 *
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public class SerializeSupport {

    private static final Logger logger = LoggerFactory.getLogger(SerializeSupport.class);
    private static final Map<Class<?>, Serializer<?>> SERIALIZER_MAP = new HashMap<>();
    private static final Map<Byte, Class<?>> TYPE_MAP = new HashMap<>();

    static {
        for (Serializer serializer : ServiceSupport.loadAll(Serializer.class)) {
            registerType(serializer.type(), serializer.getSerializeClass(), serializer);
            logger.info("Found serializer, class: {}, type: {}.",
                    serializer.getSerializeClass().getCanonicalName(),
                    serializer.type());
        }
    }

    private static <E> void registerType(byte type, Class<E> eClass, Serializer<E> serializer) {
        SERIALIZER_MAP.put(eClass, serializer);
        TYPE_MAP.put(type, eClass);
    }

    public static <E> byte [] serialize(E entry) {
        @SuppressWarnings("unchecked")
        Serializer<E> serializer = (Serializer<E>) SERIALIZER_MAP.get(entry.getClass());
        if (null == serializer) {
            throw new SerializeException(String.format("Unknown entry class type: %s", entry.getClass().toString()));
        }
        byte[] bytes = new byte[serializer.size(entry) + 1];
        bytes[0] = serializer.type();
        serializer.serialize(entry, bytes, 1, bytes.length - 1);
        return bytes;
    }

    public static <E> E parse(byte[] buffer) {
        return parse(buffer, 0, buffer.length);
    }

    private static <E> E parse(byte[] buffer, int offset, int length) {
        byte type = parseEntryType(buffer);
        @SuppressWarnings("unchecked")
        Class<E> eClass = (Class<E>) TYPE_MAP.get(type);
        if (null == eClass) {
            throw new SerializeException(String.format("Unknown entry type: %d!", type));
        } else {
            return parse(buffer, offset + 1, length - 1, eClass);
        }
    }

    @SuppressWarnings("unchecked")
    private static <E> E parse(byte [] buffer, int offset, int length, Class<E> eClass) {
        Object entry = SERIALIZER_MAP.get(eClass).parse(buffer, offset, length);
        if (eClass.isAssignableFrom(entry.getClass())) {
            return (E) entry;
        } else {
            throw new SerializeException("Type mismatch!");
        }
    }

    private static byte parseEntryType(byte[] buffer) {
        return buffer[0];
    }
}
