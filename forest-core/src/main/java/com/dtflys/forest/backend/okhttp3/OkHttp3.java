package com.dtflys.forest.backend.okhttp3;


import com.dtflys.forest.annotation.Backend;

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
public @interface OkHttp3 {
}
