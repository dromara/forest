package com.dtflys.forest.extensions;

import com.dtflys.forest.annotation.InterceptorClass;

import java.lang.annotation.*;

@Documented
@InterceptorClass(BasicAuthInterceptor.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface BasicAuth {
    /**
     * Basic auth username
     * @return
     */
    String username();

    /**
     * Basic auth password
     * @return
     */
    String password();
}
