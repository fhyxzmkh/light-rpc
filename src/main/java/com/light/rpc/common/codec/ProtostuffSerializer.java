package com.light.rpc.common.codec;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于Protostuff的序列化实现
 */
public class ProtostuffSerializer implements Serializer {

    // 缓存Schema
    private static final Map<Class<?>, Schema<?>> SCHEMA_CACHE = new ConcurrentHashMap<>();

    @Override
    public <T> byte[] serialize(T obj) {
        if (obj == null) {
            throw new IllegalArgumentException("序列化对象不能为空");
        }
        
        Class<T> clazz = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = getSchema(clazz);
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException("序列化对象 [" + obj.getClass().getName() + "] 失败: " + e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("反序列化数据不能为空");
        }
        
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            Schema<T> schema = getSchema(clazz);
            ProtostuffIOUtil.mergeFrom(data, instance, schema);
            return instance;
        } catch (Exception e) {
            throw new IllegalStateException("反序列化为对象 [" + clazz.getName() + "] 失败: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Schema<T> getSchema(Class<T> clazz) {
        Schema<T> schema = (Schema<T>) SCHEMA_CACHE.get(clazz);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(clazz);
            SCHEMA_CACHE.put(clazz, schema);
        }
        return schema;
    }
}
