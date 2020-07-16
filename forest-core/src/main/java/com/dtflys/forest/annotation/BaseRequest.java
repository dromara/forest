package com.dtflys.forest.annotation;

import java.lang.annotation.*;

/**
 * The annotation must be on interface. It allow you to make some configurations shared for all the requests in this interface.
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-03-30 16:59
 */
@Documented
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

    int retryCount() default -1;

    String keyStore() default "";

//    boolean[] logEnable() default {};

}
