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

package org.dromara.forest.annotation;

import org.dromara.forest.lifecycles.parameter.BodyLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Request Body
 * <p>该注解绑定只能绑定方法的参数。被该注解绑定的参数将作为整个请求体或请求体的一部分随请求发送到服务端。</p>
 * <p>该注解有一下几种模式：</p>
 *
 *    （1） 键值对模式:<br><br>
 *
 *          <pre class="code">
 *          void send(&#064;Body("username") String username, &#064;Body("password") String password);
 *          </pre>
 *
 *          此模式的&#064;Body注解可以修饰一个或多个参数，每个参数仅作为一个键值对。这些键值对最终会被合并成一个URL Encoded表单格式的字符串或是一个JSON对象。
 *          合并成哪种形式取决于请求的contentType属性或Content-Type请求头。<br><br>
 *
 *    （2） 对象模式：<br><br>
 *
 *          <pre class="code">
 *          void send(&#064;Body UserInfo userInfo);
 *          </pre>
 *
 *          此模式的&#064;Body注解可以修饰一个或多个自定义对象，但不能设置名称（value属性）以表明它不是一个键值对。被修饰的参数对象会被拆成一个个字段（这个对象的类中所有getter方法对应的字段），
 *          所有这些字段将像键值对一样被合并成一个URL Encoded表单格式的字符串或是一个JSON对象。
 *          同样，合并成哪种形式取决于请求的contentType属性或Content-Type请求头。<br><br>
 *
 *    （3） Map模式：<br><br>
 *
 *          <pre class="code">
 *          void send(&#064;Body Map paramMap);
 *          </pre>
 *
 *          此模式的&#064;Body注解可以修饰一个或多个Map对象，但不能设置名称（value属性）以表明它不是一个键值对。被修饰的参数对象会被拆成一个个键值对（Map中的所有有值的键值对），
 *          所有这些键值对被合并成一个URL Encoded表单格式的字符串或是一个JSON对象。
 *          同样，合并成哪种形式取决于请求的contentType属性或Content-Type请求头。<br><br>
 *
 *    （4） 字符串模式：<br><br>
 *
 *          <pre class="code">
 *          void send(&#064;Body String body);
 *          </pre>
 *
 *          此模式的&#064;Body注解可以修饰一个或多个String类型参数，但不能设置名称（value属性）以表明它不是一个键值对。
 *          被修饰的字符串参数将直接以文本形式添加到请求体中。
 *
 * @author gongjun
 * @since 2020-08-10 16:51
 */
@Documented
@ParamLifeCycle(BodyLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface Body {

    /**
     * 键值对名称（可选参数）[同name]
     * <p>有键值对名称时（有value值时），被该注解修饰的参数将被作为一个键值对合并到请求体中。</p>
     * <p>没有键值对名称时（没有设value值时），被该注解修饰的参数将被作为一个对象，根据对象的属性拆成一个个键值对，然后合并到请求体中</p>
     * @return 键值对名称
     */
    @AliasFor("name")
    String value() default "";

    /**
     * 键值对名称（可选参数）[同value]
     * <p>有键值对名称时（有value值时），被该注解修饰的参数将被作为一个键值对合并到请求体中。</p>
     * <p>没有键值对名称时（没有设value值时），被该注解修饰的参数将被作为一个对象，根据对象的属性拆成一个个键值对，然后合并到请求体中</p>
     * @return 键值对名称
     */
    @AliasFor("value")
    String name() default "";


    /**
     * The filters will do some processing for the query value before sending request.
     * @return filter names
     */
    String filter() default "";

    /**
     * 子项Content-Type
     * @return 子项Content-Type
     */
    String partContentType() default "";

    /**
     * 默认值
     * @return 默认值
     */
    String defaultValue() default "";

}
