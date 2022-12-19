package com.dtflys.forest.annotation;

import com.dtflys.forest.lifecycles.parameter.JSONQueryLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * URL JSON Query参数注解，该注解只能修饰方法的参数，被修饰的参数会被作为一个URL的Query参数并以JSON格式添加到请求的URL中的Query部分（即'?'后的那部分）
 * <p>Query参数为 [参数名1]=[参数值1](&amp;[参数名n]=[参数值n])* 的形式</P>
 * <p>[参数名] 由该注解的 value 属性表示</P>
 * <p>[参数值] 由该注解所修饰的参数的值表示</P>
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.0-BETA9
 */
@Documented
@ParamLifeCycle(JSONQueryLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface JSONQuery {

    /**
     * URL JSON Query参数的参数名（可省略）[同name]
     * <p>如有参数名，将会以 [参数名]=[参数值] 的形式添加到URL中的Query参数部分（即'?'后的那部分）</p>
     * <p>如没有参数名，将直接以 [参数值] 的形式添加到URL中的Query参数部分，</p>
     * <p>如 &#064;Query String name，且参数name的值为yyy的话，产生的URL便会是 http://xxx.xxx.xxx/xxx?yyy</p>
     * @return 参数名
     */
    @AliasFor("name")
    String value() default "";

    /**
     * URL JSON Query参数的参数名（可省略）[同value]
     * <p>如有参数名，将会以 [参数名]=[参数值] 的形式添加到URL中的Query参数部分（即'?'后的那部分）</p>
     * <p>如没有参数名，将直接以 [参数值] 的形式添加到URL中的Query参数部分，</p>
     * <p>如 &#064;Query String name，且参数name的值为yyy的话，产生的URL便会是 http://xxx.xxx.xxx/xxx?yyy</p>
     * @return 参数名
     */
    @AliasFor("value")
    String name() default "";


    /**
     * The filters will do some processing for the query value before sending request
     * @return filter names
     */
    String filter() default "";

    /**
     * 默认值
     * @return 默认值
     */
    String defaultValue() default "";

    /**
     * 排序序号
     * @return 排序序号
     */
    int order() default -1;

}
