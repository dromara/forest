package com.dtflys.forest.annotation;

import com.dtflys.forest.lifecycles.BaseAnnotationLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Base Life Cycle Class Annotation
 * 此注解将指定一个类作为某接口级别注解的生命周期处理类
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-23 23:52
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface BaseLifeCycle {

    /**
     * 命周期处理类
     * 该类会处理自定义注解所对应的请求生命周期各个环节的逻辑
     * @return
     */
    Class<? extends BaseAnnotationLifeCycle> value();
}
