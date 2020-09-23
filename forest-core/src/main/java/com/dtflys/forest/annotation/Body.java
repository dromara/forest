package com.dtflys.forest.annotation;

import com.dtflys.forest.lifecycles.parameter.BodyLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Request Body
 * <p>该注解绑定只能绑定方法的参数。被该注解绑定的参数将作为整个请求体或请求体的一部分随请求发送到服务端。</p>
 * <p>
 * 该注解有几种模式：
 *
 *    （1） 键值对模式:
 *
 *          send(@Body("username") String username, @Body("password") String password);
 *
 *          此模式的@Body注解可以修饰一个或多个参数，每个参数仅作为一个键值对。这些键值对最终会被合并成一个URL Encoded表单格式的字符串或是一个JSON对象。
 *          合并成哪种形式取决于请求的contentType属性或Content-Type请求头。
 *
 *    （2） 对象模式：
 *
 *          send(@Body UserInfo userInfo);
 *
 *          此模式的@Body注解可以修饰一个对象，但不能设置名称（value属性）以表明它不是一个键值对。被修饰的参数对象会被拆成一个个字段（这个对象的类中所有getter方法对应的字段），
 *          所有这些字段将像键值对一样被合并成一个URL Encoded表单格式的字符串或是一个JSON对象。
 *          同样，合并成哪种形式取决于请求的contentType属性或Content-Type请求头。
 * </p>
 *
 * @author gongjun
 * @since 2020-08-10 16:51
 */
@Documented
@ParamLifeCycle(BodyLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Body {

    /**
     * 键值对名称， 可选参数
     * 有键值对名称时（有value值时），被该注解修饰的参数将被作为一个键值对合并到请求体中。
     * 没有键值对名称时（没有设value值时），被该注解修饰的参数将被作为一个对象，根据对象类中的getter方法对应的字段拆成一个个键值对，然后合并到请求体中
     * @return
     */
    String value() default "";

    /**
     * The filters will do some processing for the query value before sending request.
     * @return
     */
    String filter() default "";

}
