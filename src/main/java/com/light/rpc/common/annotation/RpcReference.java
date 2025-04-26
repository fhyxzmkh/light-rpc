package com.light.rpc.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RPC引用注解，标注在引用服务的字段上
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {

    /**
     * 服务版本号
     */
    String version() default "";
    
    /**
     * 注册中心地址
     */
    String registryAddress() default "";
    
    /**
     * 服务调用超时时间，单位毫秒
     */
    long timeout() default 5000;
    
    /**
     * 是否使用直连模式（不通过注册中心）
     */
    boolean directAddress() default false;
    
    /**
     * 直连地址（当directAddress=true时有效）
     */
    String address() default "";
}
