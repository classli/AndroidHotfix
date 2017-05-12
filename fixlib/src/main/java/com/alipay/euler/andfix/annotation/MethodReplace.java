package com.alipay.euler.andfix.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotaion for Replaced method, Automatic tool generation
 * Created by weixiao.ll on 17/5/9.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodReplace {
    String clazz();

    String method();
}
