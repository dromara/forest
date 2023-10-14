package com.dtflys.forest.springcloud;

import com.dtflys.forest.annotation.MethodLifeCycle;

import java.lang.annotation.*;

/**
 * @author changjin wei(魏昌进)
 * @since 1.5.33
 */
@Documented
@MethodLifeCycle(LoadBalancerLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface LoadBalancer {

    /**
     * 微服务的serviceId
     * @return serviceId
     */
    String value() default "";

}
