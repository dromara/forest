package com.dtflys.forest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-03-30 16:59
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BaseRequest {

    String contentType() default "";

    String contentEncoding() default "";

    String[] headers() default {};

    Class<?>[] interceptor() default {};

    int timeout() default -1;

    int retryCount() default -1;

    String keyStore() default "";

    boolean[] logEnable() default {};

}
