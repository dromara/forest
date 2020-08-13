/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Jun Gong
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.dtflys.forest.annotation;


import com.dtflys.forest.lifecycles.RequestLifeCycle;

import java.lang.annotation.*;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2016-05-12
 */
@Documented
@com.dtflys.forest.annotation.LifeCycle(RequestLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Request {

    /**
     * target http url
     * @return
     */
    String url();

    /**
     * http method type: <br>
     *     GET POST PUT HEAD OPTIONS DELETE PATCH TRACE
     * @return
     */
    String type() default "";

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
