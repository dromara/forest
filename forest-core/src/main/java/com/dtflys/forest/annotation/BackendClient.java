package com.dtflys.forest.annotation;

import com.dtflys.forest.lifecycles.method.BackendClientLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Forest后端框架 Client 注解
 * <p>可用于指定请求接口所对应的后端 Client 对象是否缓存，列如：
 * <pre>
 *     &#064;BackendClient(cache = true)
 *     &#064;Post("/")
 *     public String send();
 * </pre>
 * 或者
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @since 1.5.23
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface BackendClient {

    /**
     * 后端框架的 Client 对象是否缓存
     * @return {@code true}: 缓存, {@code false}: 不缓存
     */
    boolean cache() default true;

}
