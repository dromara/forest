package com.dtflys.forest.annotation;

import com.dtflys.forest.lifecycles.method.PostRequestLifeCycle;

import java.lang.annotation.*;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-13 23:41
 */
@Documented
@MethodLifeCycle(PostRequestLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PostRequest {

    /**
     * target http url
     * @return
     */
    String url();

    /**
     * type of response data: <br>
     *     text json xml <br>
     * default value is "auto"
     * @return
     */
    String dataType() default "auto";

    /**
     * whether can use async http request or not
     * @return
     */
    boolean async() default false;

    int timeout() default -1;

    /**
     * Class of retryer
     * @return
     */
    Class retryer() default Object.class;

    /**
     * max count to retry
     * @return
     */
    int retryCount() default 0;

    int maxRetryInterval() default -1;

    /**
     * Content Type
     * @return
     */
    String contentType() default "";

    /**
     * Content Encoding
     * @return
     */
    String contentEncoding() default "";

    /**
     * Charset, Default is UTF-8
     * @return
     */
    String charset() default "";

    /**
     * reqest headers: <br>
     *     use the key-value format: key: value <br>
     *     <pre>
     *         headers = "Content-Type: application/json"
     *     </pre>
     *     multiple headers <br>
     *     <pre>
     *         headers = {
     *            "Content-Type: application/json",
     *            "Accept: text/plain"
     *         }
     *     </pre>
     *     variables and parameters <br>
     *     <pre>
     *         headers = {"Accept: ${value}"}
     *     <pre/>
     *
     * @return
     */
    String[] headers() default {};

    Class<?>[] interceptor() default {};

    String[] data() default {};

    long progressStep() default -1L;

    Class<?> decoder() default Object.class;

    /**
     * KeyStore Id
     * @return
     */
    String keyStore() default "";

    boolean logEnabled() default false;

}
