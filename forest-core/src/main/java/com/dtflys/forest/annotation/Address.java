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

import com.dtflys.forest.callback.AddressSource;
import com.dtflys.forest.lifecycles.method.AddressLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 主机地址注解
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @since 1.5.2
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Address {

    /**
     * HTTP协议头
     * <p>协议头可以是：
     * <ul>
     *     <li>http</li>
     *     <li>https</li>
     * </ul>
     * @return HTTP协议头
     */
    String scheme() default "";

    /**
     * 主机地址(主机名/ip地址)
     * @return 主机地址
     */
    String host() default "";

    /**
     * 主机端口号
     * @return 主机端口号
     */
    String port() default "";

    /**
     * URL根路径
     * <p>两种使用方式：
     *
     * <ul>
     *     <li>
     *         <p>1. 短路径
     *         <pre>
     *             basePath = "/abc"
     *             basePath = "/abc/123"
     *         </pre>
     *     </li>
     *     <li>
     *         <p>2. 完整URL
     *         <pre>
     *             basePath = "http://localhost:8080/abc"
     *             basePath = "http://localhost:8080/abc/123"
     *         </pre>
     *     </li>
     * </ul>
     *
     * <p>在使用完整URL时候会覆盖原有的 schema, host, 以及 port 属性
     * <p>但若在本注解中设置以上三个属性，则它们的优先级高于 basePath 属性
     *
     * @return URL根路径
     */
    String basePath() default "";

    /**
     * 动态构建主机地址信息的回调函数接口类
     * @return 回调函数接口类
     */
    Class<? extends AddressSource> source() default AddressSource.class;

}
