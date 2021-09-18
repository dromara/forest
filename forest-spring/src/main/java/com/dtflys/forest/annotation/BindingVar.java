package com.dtflys.forest.annotation;

import com.dtflys.forest.lifecycles.method.DeleteRequestLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Forest 全局变量绑定标签
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.2
 */
@Documented
@MethodLifeCycle(DeleteRequestLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BindingVar {

    /**
     * 绑定的变量名
     */
    String value();

    /**
     * 所绑定的 ForestConfiguration Bean Id
     */
    String configuration() default "";

}