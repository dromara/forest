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

import com.dtflys.forest.lifecycles.method.PatchRequestLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Patch请求信息定义注解，该注解只能修饰方法，被修饰的方法会自动被动态代理。当调用被修饰的方法时就会执行Forest动态的代理的代码，
 * 也就会自动执行组装请求、发送请求、接受请求响应信息等任务。
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.4.0
 */
@Documented
@MethodLifeCycle(PatchRequestLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface Patch {

    /**
     * 目标请求URL [同url属性]
     * @return URL字符串
     */
    @AliasFor("url")
    String value() default "";

    /**
     * 目标请求URL [同value属性]
     * @return URL字符串
     */
    @AliasFor("value")
    String url() default "";

    /**
     * type of response data:
     * <p>    text json xml binary auto
     * <p>default value is "auto"
     * @return type of response data
     */
    String dataType() default "auto";

    /**
     * whether can use async http request or not
     * @return {@code true}: async, {@code false}: sync
     */
    boolean async() default false;

    /**
     * 请求超时时间, 单位为毫秒
     * @return 请求超时时间
     */
    int timeout() default -1;

    /**
     * 请求连接超时时间, 单位为毫秒
     * @return 请求连接超时时间
     */
    int connectTimeout() default -1;

    /**
     * 请求读取超时时间, 单位为毫秒
     * @return 读取超时时间
     */
    int readTimeout() default -1;

    /**
     * SSL protocol
     * @return SSL protocol
     */
    String sslProtocol() default "";

    /**
     * Class of retryer
     * @return Class of retryer
     */
    Class retryer() default Object.class;

    /**
     * Max count to retry
     * @return Max count to retry
     */
    @Deprecated
    int retryCount() default -1;

    /**
     * Max count to retry
     * @return Max count to retry
     */
    int maxRetryInterval() default -1;

    /**
     * Content Type of request
     * @return Content Type
     */
    String contentType() default "";

    /**
     * Content Encoding of request
     * @return Content Encoding
     */
    String contentEncoding() default "";

    /**
     * User Agent of request
     * @return User Agent
     */
    String userAgent() default "";

    /**
     * Charset, Default is UTF-8
     * @return Charset
     */
    String charset() default "";

    /**
     * Response Encoding
     * <p>该属性不填的情况下，根据响应头中的 Content-Encoding 来确定响应内容的编码
     * @return Response Encoding
     */
    String responseEncoding() default "";

    /**
     * request headers: <br>
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
     * @return headers
     */
    String[] headers() default {};

    /**
     * 拦截器类列表
     * @return 拦截器类列表
     */
    Class<?>[] interceptor() default {};

    /**
     * 请求数据
     * @return 请求数据
     */
    String[] data() default {};

    /**
     * 上传/下载进度步长
     * @return 上传/下载进度步长
     */
    long progressStep() default -1L;

    /**
     * 请求序列化器
     * @return 请求序列化器
     */
    Class<?> encoder() default Object.class;

    /**
     * 请求反序列化器
     * @return 请求反序列化器
     */
    Class<?> decoder() default Object.class;

    /**
     * KeyStore Id
     * @return KeyStore Id
     */
    String keyStore() default "";

    /**
     * 是否打印请求日志
     * @return {@code true}: 打印, {@code false}: 不打印
     */
    @Deprecated
    boolean logEnabled() default false;

}
