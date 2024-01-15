package com.dtflys.forest.annotation;

import com.dtflys.forest.callback.SuccessWhen;
import com.dtflys.forest.lifecycles.method.RetryLifeCycle;
import com.dtflys.forest.lifecycles.method.SuccessLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 请求成功注解
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @since 1.5.2
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface Success {

    /**
     * 请求成功条件回调函数类
     * @return 请求成功条件回调函数类
     */
    Class<? extends SuccessWhen> condition();
}
