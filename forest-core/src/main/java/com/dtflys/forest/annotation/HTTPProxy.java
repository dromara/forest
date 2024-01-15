package com.dtflys.forest.annotation;

import com.dtflys.forest.callback.HTTPProxySource;
import com.dtflys.forest.http.ForestProxyType;
import com.dtflys.forest.lifecycles.proxy.HTTPProxyLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * HTTP正向代理注解
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @since 1.5.0-BETA5
 */
@Documented
@RequestAttributes
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface HTTPProxy {

    /**
     * 代理类型
     * @return 代理类型
     */
    ForestProxyType type() default ForestProxyType.HTTP;

    /**
     * 代理服务主机地址
     * @return 代理服务主机地址
     */
    String host() default "";

    /**
     * 代理服务端口号
     * @return 代理服务端口号
     */
    String port() default "80";

    /**
     * 代理用户名
     * @return 代理用户名
     */
    String username() default "";

    /**
     * 代理密码
     * @return 代理密码
     */
    String password() default "";

    /**
     * http proxy headers
     *
     * @return http proxy headers
     */
    String[] headers() default {};

    /**
     * 动态构建正向代理信息的回调函数接口类
     * @return 回调函数接口类
     */
    Class<? extends HTTPProxySource> source() default HTTPProxySource.class;

}
