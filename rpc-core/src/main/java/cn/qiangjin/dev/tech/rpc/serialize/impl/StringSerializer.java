package cn.qiangjin.dev.tech.rpc.serialize.impl;

import cn.qiangjin.dev.tech.rpc.serialize.Serializer;

import java.nio.charset.StandardCharsets;

/**
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public class StringSerializer implements Serializer<String> {

    @Override
    public int size(String entry) {
        return entry.getBytes(StandardCharsets.UTF_8).length;
    }

    @Override
    public void serialize(String entry, byte[] bytes, int offset, int length) {
        byte[] entryBytes = entry.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(entryBytes, 0, bytes, offset, entryBytes.length);
    }

    @Override
    public String parse(byte[] bytes, int offset, int length) {
        return new String(bytes, offset, length, StandardCharsets.UTF_8);
    }

    @Override
    public byte type() {
        return Types.TYPE_STRING;
    }

    @Override
    public Class<String> getSerializeClass() {
        return String.class;
    }
}
