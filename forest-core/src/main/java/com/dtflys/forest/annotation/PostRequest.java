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

import com.dtflys.forest.lifecycles.method.PostRequestLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Post请求信息定义注解，该注解只能修饰方法，被修饰的方法会自动被动态代理。当调用被修饰的方法时就会执行Forest动态的代理的代码，
 * 也就会自动执行组装请求、发送请求、接受请求响应信息等任务。
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.4.0
 */
@Documented
@MethodLifeCycle(PostRequestLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PostRequest {

    /**
     * 目标请求URL [同url属性]
     */
    @AliasFor("url")
    String value() default "";

    /**
     * 目标请求URL [同value属性]
     */
    @AliasFor("value")
    String url() default "";

    /**
     * type of response data: <br>
     *     text json xml <br>
     * default value is "auto"
     */
    String dataType() default "auto";

    /**
     * whether can use async http request or not
     */
    boolean async() default false;

    int timeout() default -1;

    /**
     * SSL protocol
     */
    String sslProtocol() default "";

    /**
     * Class of retryer
     */
    Class retryer() default Object.class;

    /**
     * max count to retry
     */
    int retryCount() default -1;

    int maxRetryInterval() default -1;

    /**
     * Content Type
     */
    String contentType() default "";

    /**
     * Content Encoding
     */
    String contentEncoding() default "";

    /**
     * User Agent
     */
    String userAgent() default "";

    /**
     * Charset, Default is UTF-8
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
     *     </pre>
     */
    String[] headers() default {};

    Class<?>[] interceptor() default {};

    String[] data() default {};

    long progressStep() default -1L;

    Class<?> decoder() default Object.class;

    /**
     * KeyStore Id
     */
    String keyStore() default "";

    boolean logEnabled() default false;

}
