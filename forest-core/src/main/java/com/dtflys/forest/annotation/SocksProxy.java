package com.dtflys.forest.annotation;

import com.dtflys.forest.callback.HTTPProxySource;
import com.dtflys.forest.http.ForestProxyType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@HTTPProxy(type = ForestProxyType.SOCKS)
@RequestAttributes
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface SocksProxy {

    /**
     * 代理服务主机地址
     * @return 代理服务主机地址
     */
    @OverrideAttribute
    String host() default "";

    /**
     * 代理服务端口号
     * @return 代理服务端口号
     */
    @OverrideAttribute
    String port() default "80";

    /**
     * 代理用户名
     * @return 代理用户名
     */
    @OverrideAttribute
    String username() default "";

    /**
     * 代理密码
     * @return 代理密码
     */
    @OverrideAttribute
    String password() default "";


    /**
     * 动态构建正向代理信息的回调函数接口类
     * @return 回调函数接口类
     */
    @OverrideAttribute
    Class<? extends HTTPProxySource> source() default HTTPProxySource.class;

}
