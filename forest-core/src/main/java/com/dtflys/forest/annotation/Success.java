package com.dtflys.forest.annotation;

import com.dtflys.forest.callback.SuccessWhen;
import com.dtflys.forest.lifecycles.method.RetryLifeCycle;
import com.dtflys.forest.lifecycles.method.SuccessLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@MethodLifeCycle(SuccessLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Success {

    Class<? extends SuccessWhen> condition();
}
