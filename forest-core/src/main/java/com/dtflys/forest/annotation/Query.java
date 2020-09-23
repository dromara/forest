package com.dtflys.forest.annotation;

import com.dtflys.forest.lifecycles.parameter.QueryLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * URL Query参数注解，该注解只能修饰方法的参数，被修饰的参数会被作为一个URL的Query参数添加到请求的URL中的Query部分（即'?'后的那部分）
 * Query参数将会以 [参数名]=[参数值] 的形式添加到URL中的Query参数部分（即'?'后的那部分）
 * [参数名] 由该注解的 value 属性表示
 * [参数值] 由该注解所修饰的参数的值表示
 * <p>
 * 该注解有一下几种模式：
 *
 *    （1） 键值对模式:
 *
 *          send(@Query("username") String username, @Query("password") String password);
 *
 *          此模式的@Query注解可以修饰一个或多个参数，每个参数仅作为一个键值对。这些键值对最终会被作为一个URL的Query参数添加到请求的URL参数中
 *
 *    （2） 对象模式：
 *
 *          send(@Query UserInfo userInfo);
 *
 *          此模式的@Query注解可以修饰一个或多个自定义对象，但不能设置名称（value属性）以表明它不是一个键值对。被修饰的参数对象会被拆成一个个字段（这个对象类中所有getter方法对应的字段），
 *          所有这些字段将像键值对一样被作为一个URL的Query参数添加到请求的URL参数中
 *
 *    （3） Map模式：
 *
 *          send(@Query Map headerMap);
 *
 *          此模式的@Query注解可以修饰一个或多个Map对象，但不能设置名称（value属性）以表明它不是一个键值对。被修饰的参数对象会被拆成一个个键值对（Map中的所有有值的键值对），
 *          所有这些键值对被作为一个URL的Query参数添加到请求的URL参数中
 *
 *    （4） 字符串模式：
 *
 *          send(@Query String query);
 *
 *          此模式的@Query注解可以修饰一个或多个String类型参数，但不能设置名称（value属性）以表明它不是一个键值对。
 *          被修饰的字符串参数将直接以 [参数值] 的形式添加到URL中的Query参数部分
 *
 * </p>
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
     * URL Query参数的参数名（可省略）
     * 如有参数名，将会以 [参数名]=[参数值] 的形式添加到URL中的Query参数部分（即'?'后的那部分）
     * 如没有参数名，将直接以 [参数值] 的形式添加到URL中的Query参数部分，
     * 如 @Query String name，且参数name的值为yyy的话，产生的URL便会是 http://xxx.xxx.xxx/xxx?yyy
     *
     * @return
     */
    String value() default "";

    /**
     * The filters will do some processing for the query value before sending request.
     * @return
     */
    String filter() default "";

}
