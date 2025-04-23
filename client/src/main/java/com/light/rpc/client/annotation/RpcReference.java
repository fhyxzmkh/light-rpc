package com.light.rpc.client.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcReference {
    boolean useLocalCall() default false;
}