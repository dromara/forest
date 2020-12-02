package com.dtflys.forest.extensions;

import com.dtflys.forest.annotation.MethodLifeCycle;
import com.dtflys.forest.annotation.RequestAttributes;
import com.dtflys.forest.lifecycles.authorization.OAuth2LifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该注解修饰的方法或接口类，将忽略所有的拦截器
 */
@Documented
@MethodLifeCycle(OAuth2LifeCycle.class)
@RequestAttributes
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface IgnoreAllIntercetpors {
}
