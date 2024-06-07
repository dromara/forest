package com.dtflys.forest.annotation;

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
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface BindingVar {

    /**
     * 绑定的变量名
     * @return 绑定的变量名
     */
    String value() default "";

    /**
     * 所绑定的 ForestConfiguration Bean Id
     * @return 所绑定的 ForestConfiguration Bean Id
     */
    String configuration() default "";

}
