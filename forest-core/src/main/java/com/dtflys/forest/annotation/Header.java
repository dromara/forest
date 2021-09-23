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

import com.dtflys.forest.lifecycles.parameter.HeaderLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 请求头注解，该注解只能修饰方法的参数，被修饰的参数会被作为请求头信息添加到请求中并发送到服务端
 * <p>该注解有一下几种模式：</p>
 *
 *    （1） 键值对模式:<br><br>
 *
 *          <pre class="code">
 *          void send(&#064;Header("Access-Token") String token, &#064;Header("Accept") String accept);
 *          </pre>
 *
 *          此模式的&#064;Header注解可以修饰一个或多个参数，每个参数仅作为一个键值对。这些键值对最终会被作为一个请求头信息添加到请求中。<br><br>
 *
 *    （2） 对象模式：<br><br>
 *
 *          <pre class="code">
 *          void send(&#064;Header HeaderInfo headerInfo);
 *          </pre>
 *
 *          此模式的&#064;Header注解可以修饰一个或多个自定义对象，但不能设置名称（value属性）以表明它不是一个键值对。被修饰的参数对象会被拆成一个个字段（这个对象类中所有getter方法对应的字段），
 *          所有这些字段将像键值对一样被作为一个请求头信息的集合添加到请求中。<br><br>
 *
 *    （3） Map模式：<br><br>
 *
 *          <pre class="code">
 *          void send(&#064;Header Map headerMap);
 *          </pre>
 *
 *          此模式的&#064;Header注解可以修饰一个或多个Map对象，但不能设置名称（value属性）以表明它不是一个键值对。被修饰的参数对象会被拆成一个个键值对（Map中的所有有值的键值对），
 *          所有这些键值对被作为一个请求头信息的集合添加到请求中。
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-12 22:26
 */
@Documented
@ParamLifeCycle(HeaderLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
public @interface Header {

    /**
     * 请求头名（可省略）[同value]
     * @return 请求头名
     */
    @AliasFor("name")
    String value() default "";

    /**
     * 请求头名（可省略）[同name]
     * @return 请求头名
     */
    @AliasFor("value")
    String name() default "";

    /**
     * 默认值
     * @return 默认值
     */
    String defaultValue() default "";

}
