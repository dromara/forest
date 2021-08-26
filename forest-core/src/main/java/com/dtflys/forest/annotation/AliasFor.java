package com.dtflys.forest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解属性别名
 * <p>为其他注解中定义的属性定义别名，当该属性为空值时将引用其别名对应的属性值</p>
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.0-BETA9
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AliasFor {

    /**
     * 属性所对应的别名
     */
    String value();
}
