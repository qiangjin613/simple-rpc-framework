package cn.qiangjin.dev.tech.rpc.serialize;

/**
 * 所有的序列化实现类都实现这个接口
 *
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public interface Serializer<T> {

    /**
     * 计算对象序列化后的长度，主要用于申请存放序列化数据的字节数组
     *
     * @param entry 待序列化的对象
     * @return 对象序列化后的长度
     */
    int size(T entry);

    /**
     * 序列化对象。将给定的对象序列化成字节数组
     *
     * @param entry 待序列化的对象
     * @param bytes 存放序列化数据的字节数组
     * @param offset 数组的偏移量，从这个位置开始写入序列化数据
     * @param length 对象序列化后的长度，也就是{@link Serializer#size(java.lang.Object)}方法的返回值。
     */
    void serialize(T entry, byte[] bytes, int offset, int length);

    /**
     * 反序列化对象
     *
     * @param bytes 存放序列化数据的字节数组
     * @param offset 数组的偏移量，从这个位置开始写入序列化数据
     * @param length 对象序列化后的长度
     * @return 反序列化之后生成的对象
     */
    T parse(byte[] bytes, int offset, int length);

    /**
     * 用一个字节标识对象类型，每种类型的数据应该具有不同的类型值
     *
     * @return
     */
    byte type();

    /**
     * 返回序列化对象类型的Class对象。
     *
     * @return
     */
    Class<T> getSerializeClass();
}
