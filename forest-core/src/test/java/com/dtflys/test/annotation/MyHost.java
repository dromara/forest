package com.dtflys.test.annotation;


import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.OverrideAttribute;
import com.dtflys.forest.annotation.RequestAttributes;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@RequestAttributes
@Address
public @interface MyHost {

    @OverrideAttribute("host")
    String value();

    @OverrideAttribute
    String port() default "{port}";
}
