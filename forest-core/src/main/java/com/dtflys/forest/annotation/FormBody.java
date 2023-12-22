package com.dtflys.forest.annotation;

import com.dtflys.forest.lifecycles.parameter.FormBodyLifeCycle;
import com.dtflys.forest.lifecycles.parameter.JSONBodyLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Request Form Body
 * <p>该注解绑定只能绑定方法的参数。被该注解绑定的参数将被解析为表单字符串，并作为整个请求体或请求体的一部分随请求发送到服务端。</p>
 * <p>同时，请求的 Content-Type 自动被设置为 application/x-www-form-urlencoded</p>
 * <p>该注解有一下几种模式：</p>
 *
 *    （1） 键值对模式:<br><br>
 *
 *          <pre class="code">
 *          void send(&#064;FormBody("username") String username, &#064;FormBody("password") String password);
 *          </pre>
 *
 *          此模式的&#064;FormBody注解可以修饰一个或多个参数，每个参数仅作为一个键值对。这些键值对最终会被合并成一个表单对象。<br><br>
 *
 *    （2） 对象模式：<br><br>
 *
 *          <pre class="code">
 *          void send(&#064;FormBody UserInfo userInfo);
 *          </pre>
 *
 *          此模式的&#064;FormBody注解可以修饰一个或多个自定义对象，但不能设置名称（value属性）以表明它不是一个键值对。被修饰的参数对象会被拆成一个个字段（这个对象的类中所有getter方法对应的字段），
 *          所有这些字段将像键值对一样被合并成一个表单对象。<br><br>
 *
 *    （3） Map模式：<br><br>
 *
 *          <pre class="code">
 *          void send(&#064;FormBody Map paramMap);
 *          </pre>
 *
 *          此模式的&#064;FormBody注解可以修饰一个或多个Map对象，但不能设置名称（value属性）以表明它不是一个键值对。被修饰的参数对象会被拆成一个个键值对（Map中的所有有值的键值对），
 *          所有这些键值对被合并成一个表单对象。<br><br>
 *
 *    （4） 字符串模式：<br><br>
 *
 *          <pre class="code">
 *          void send(&#064;FormBody String body);
 *          </pre>
 *
 *          此模式的&#064;FormBody注解可以修饰一个或多个String类型参数，但不能设置名称（value属性）以表明它不是一个键值对。
 *          同时参数值也必须是一个合法的表单字符串。
 *          被修饰的字符串参数将直接以文本形式添加到请求体中。
 *
 * @author gongjun
 * @since 1.5.0-BETA9
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface FormBody {

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
     * 默认值
     * @return 默认值
     */
    String defaultValue() default "";

}
