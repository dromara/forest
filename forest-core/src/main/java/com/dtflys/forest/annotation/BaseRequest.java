package com.dtflys.forest.annotation;

import com.dtflys.forest.lifecycles.base.BaseRequestLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

/**
 * 接口级别请求配置信息注解<br>
 * The annotation must be on an interface. It allows you to make some configurations shared for all the requests in this interface.<br>
 * 该注解必须绑定在某一个接口类上。在该注解中配置的参数信息将会被次注解绑定的接口中所有方法的请求所共享，
 * 它可以覆盖全局级别的请求配置信息，但不能覆盖方法级别的请求参数信息
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-03-30 16:59
 */
@Documented
@BaseLifeCycle(BaseRequestLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BaseRequest {

    /**
     * Base URL
     * @return
     */
    String baseURL() default "";

    /**
     * Base Content Type
     * @return
     */
    String contentType() default "";

    /**
     * Base Content Encoding
     * @return
     */
    String contentEncoding() default "";

    /**
     * Base User Agent
     * @return
     */
    String userAgent() default "";

    String charset() default "UTF-8";

    /**
     * Base Headers
     * @return
     */
    String[] headers() default {};

    /**
     * Base Interceptor
     * @return
     */
    Class<?>[] interceptor() default {};

    int timeout() default -1;

    /**
     * SSL protocol
     */
    String sslProtocol() default "";

    /**
     * Class of retryer
     * @return
     */
    Class retryer() default Object.class;

    int retryCount() default -1;

    long maxRetryInterval() default -1;

    String keyStore() default "";

//    boolean[] logEnable() default {};

}
