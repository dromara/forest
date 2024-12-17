package com.dtflys.forest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SSE 的 data 事件消息处理方法注解
 * <p>被该注解修饰的方法，会作为 SSE 的 data 事件消息处理函数，在监听到 name 为 data 的 SSE 消息时被调用</p>
 *
 * @since 1.6.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SSEDataMessage {

    /**
     * 消息值的正则表达式
     * @return 值的正则表达式
     */
    String valueRegex() default "";

    /**
     * 消息值的前缀
     * @return 消息值的前缀
     */
    String valuePrefix() default "";

    /**
     * 消息值的后缀
     * @return 消息值的后缀
     */
    String valuePostfix() default "";
}
