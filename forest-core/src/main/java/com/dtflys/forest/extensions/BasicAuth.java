package com.dtflys.forest.extensions;

import com.dtflys.forest.annotation.RequestAttributes;
import com.dtflys.forest.annotation.LifeCycle;
import com.dtflys.forest.lifecycles.BasicAuthLifeCycle;

import java.lang.annotation.*;

@Documented
@LifeCycle(BasicAuthLifeCycle.class)
@RequestAttributes
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
