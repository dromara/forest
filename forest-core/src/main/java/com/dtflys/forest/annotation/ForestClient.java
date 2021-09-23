package com.dtflys.forest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Forest客户端注解
 * <p>用于标识一个接口类是一个Forest客户端接口
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @since 1.5.3
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ForestClient {
}
