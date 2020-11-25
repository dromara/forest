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

import com.dtflys.forest.lifecycles.parameter.QueryLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * URL Query参数注解，该注解只能修饰方法的参数，被修饰的参数会被作为一个URL的Query参数添加到请求的URL中的Query部分（即'?'后的那部分）
 * <p>Query参数为 [参数名1]=[参数值1](&amp;[参数名n]=[参数值n])* 的形式</P>
 * <p>[参数名] 由该注解的 value 属性表示</P>
 * <p>[参数值] 由该注解所修饰的参数的值表示</P>
 * <p>该注解有一下几种模式：</p>
 *
 *    （1） 键值对模式: <br><br>
 *
 *          <pre class="code">
 *          void send(&#064;Query("username") String username, &#064;Query("password") String password);
 *          </pre>
 *
 *          此模式的&#064;Query注解可以修饰一个或多个参数，每个参数仅作为一个键值对。这些键值对最终会被作为一个URL的Query参数添加到请求的URL参数中<br><br>
 *
 *    （2） 对象模式：<br><br>
 *
 *          <pre class="code">
 *          void send(&#064;Query UserInfo userInfo);
 *          </pre>
 *
 *          此模式的&#064;Query注解可以修饰一个或多个自定义对象，但不能设置名称（value属性）以表明它不是一个键值对。被修饰的参数对象会被拆成一个个字段（这个对象类中所有getter方法对应的字段），
 *          所有这些字段将像键值对一样被作为一个URL的Query参数添加到请求的URL参数中 <br><br>
 *
 *    （3） Map模式：<br><br>
 *
 *          <pre class="code">
 *          void send(&#064;Query Map headerMap);
 *          </pre>
 *
 *          此模式的&#064;Query注解可以修饰一个或多个Map对象，但不能设置名称（value属性）以表明它不是一个键值对。被修饰的参数对象会被拆成一个个键值对（Map中的所有有值的键值对），
 *          所有这些键值对被作为一个URL的Query参数添加到请求的URL参数中 <br><br>
 *
 *    （4） 字符串模式：<br><br>
 *
 *          <pre class="code">
 *          void send(&#064;Query String query);
 *          </pre>
 *
 *          此模式的&#064;Query注解可以修饰一个或多个String类型参数，但不能设置名称（value属性）以表明它不是一个键值对。
 *          被修饰的字符串参数将直接以 [参数值] 的形式添加到URL中的Query参数部分
 *
 * @author gongjun
 * @since 2020-08-03
 */
@Documented
@ParamLifeCycle(QueryLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Query {

    /**
     * URL Query参数的参数名（可省略）[同name]
     * <p>如有参数名，将会以 [参数名]=[参数值] 的形式添加到URL中的Query参数部分（即'?'后的那部分）</p>
     * <p>如没有参数名，将直接以 [参数值] 的形式添加到URL中的Query参数部分，</p>
     * <p>如 &#064;Query String name，且参数name的值为yyy的话，产生的URL便会是 http://xxx.xxx.xxx/xxx?yyy</p>
     */
    @AliasFor("name")
    String value() default "";

    /**
     * URL Query参数的参数名（可省略）[同value]
     * <p>如有参数名，将会以 [参数名]=[参数值] 的形式添加到URL中的Query参数部分（即'?'后的那部分）</p>
     * <p>如没有参数名，将直接以 [参数值] 的形式添加到URL中的Query参数部分，</p>
     * <p>如 &#064;Query String name，且参数name的值为yyy的话，产生的URL便会是 http://xxx.xxx.xxx/xxx?yyy</p>
     */
    @AliasFor("value")
    String name() default "";

    /**
     * The filters will do some processing for the query value before sending request.
     */
    String filter() default "";


}
