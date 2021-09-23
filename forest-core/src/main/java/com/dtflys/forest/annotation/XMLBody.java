package com.dtflys.forest.annotation;

import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.lifecycles.parameter.XMLBodyLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Request XML Body
 * <p>该注解绑定只能绑定方法的参数。被该注解绑定的参数将被解析为XML字符串，并作为整个请求体或请求体的一部分随请求发送到服务端。</p>
 * <p>同时，请求的 Content-Type 自动被设置为 application/xml</p>
 * <p>该注解有一下几种模式：</p>
 *
 *    （1） 对象模式：<br>
 *
 *          <pre class="code">
 *          void send(&#064;XMLBody UserInfo userInfo);
 *          </pre>
 *
 *          此模式的&#064;XMLBody注解可以修饰一个或多个自定义对象，但不能设置名称（value属性）以表明它不是一个键值对。被修饰的参数对象会被拆成一个个字段（这个对象的类中所有getter方法对应的字段），
 *          所有这些字段将像键值对一样被合并成一个XML对象。<br><br>
 *
 *    （2） 字符串模式：<br>
 *
 *          <pre class="code">
 *          void send(&#064;XMLBody String body);
 *          </pre>
 *
 *          此模式的&#064;XMLBody注解可以修饰一个或多个String类型参数，但不能设置名称（value属性）以表明它不是一个键值对。
 *          同时参数值也必须是一个合法的XML字符串。
 *          被修饰的字符串参数将直接以文本形式添加到请求体中。
 *
 * @author gongjun
 * @since 1.5.0-BETA9
 */
@Documented
@ParamLifeCycle(XMLBodyLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
public @interface XMLBody {

    /**
     * The filters will do some processing for the query value before sending request.
     */
    String filter() default "";

    /**
     * 子项Content-Type
     */
    String partContentType() default ContentType.APPLICATION_XML;

}
