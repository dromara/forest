package org.dromara.forest.backend.okhttp3;


import org.dromara.forest.annotation.Backend;
import org.dromara.forest.annotation.MethodLifeCycle;
import org.dromara.forest.backend.okhttp3.conn.OkHttp3ConnectionManager;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 后端注解: OkHttp3
 * <p>使用该注解可以指定请求后端框架为 OkHttp3
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.5
 */
@Backend(OkHttp3Backend.NAME)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@MethodLifeCycle(OkHttp3LifeCycle.class)
public @interface OkHttp3 {

    /**
     * 后端 HttpClient 工厂
     *
     * @return HttpClient 工厂类
     * @since 1.5.23
     */
    Class<? extends OkHttpClientProvider> client() default OkHttp3ConnectionManager.DefaultOkHttpClientProvider.class;

}
