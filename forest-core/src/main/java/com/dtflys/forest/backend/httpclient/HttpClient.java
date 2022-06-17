package com.dtflys.forest.backend.httpclient;


import com.dtflys.forest.annotation.Backend;
import com.dtflys.forest.annotation.MethodLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 后端注解: HttpClient
 * <p>使用该注解可以指定请求后端框架为 HttpClient
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.5
 */
@Backend(HttpclientBackend.NAME)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@MethodLifeCycle(HttpClientLifeCycle.class)
public @interface HttpClient {

    /**
     * 后端 HttpClient 工厂
     *
     * @since 1.5.23
     */
    Class<? extends HttpClientProvider> client() default HttpClientProvider.class;
}
