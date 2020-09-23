package com.dtflys.forest.annotation;

import com.dtflys.forest.lifecycles.parameter.HeaderLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 请求头注解，该注解只能修饰方法的参数，被修饰的参数会被作为请求头信息添加到请求中并发送到服务端
 * <p>
 * 该注解有一下几种模式：
 *
 *    （1） 键值对模式:
 *
 *          void send(@Header("Access-Token") String token, @Header("Accept") String accept);
 *
 *          此模式的@Header注解可以修饰一个或多个参数，每个参数仅作为一个键值对。这些键值对最终会被作为一个请求头信息添加到请求中。
 *
 *    （2） 对象模式：
 *
 *          void send(@Header HeaderInfo headerInfo);
 *
 *          此模式的@Header注解可以修饰一个或多个自定义对象，但不能设置名称（value属性）以表明它不是一个键值对。被修饰的参数对象会被拆成一个个字段（这个对象类中所有getter方法对应的字段），
 *          所有这些字段将像键值对一样被作为一个请求头信息的集合添加到请求中。
 *
 *    （3） Map模式：
 *
 *          void send(@Header Map headerMap);
 *
 *          此模式的@Header注解可以修饰一个或多个Map对象，但不能设置名称（value属性）以表明它不是一个键值对。被修饰的参数对象会被拆成一个个键值对（Map中的所有有值的键值对），
 *          所有这些键值对被作为一个请求头信息的集合添加到请求中。
 * </p>
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-12 22:26
 */
@Documented
@ParamLifeCycle(HeaderLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Header {

    /**
     * Request header name
     * @return
     */
    String value() default "";


}
