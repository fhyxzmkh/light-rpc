package com.light.rpc.common.codec;

/**
 * 序列化接口
 */
public interface Serializer {

    /**
     * 序列化（对象 -> 字节数组）
     */
    <T> byte[] serialize(T obj);

    /**
     * 反序列化（字节数组 -> 对象）
     */
    <T> T deserialize(byte[] data, Class<T> cls);
}
