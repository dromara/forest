package com.dtflys.forest.annotation;

import java.lang.annotation.*;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-12 22:23
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface BodyObject {

    /**
     * The filters will do some processing for the object value before sending request.
     * @return
     */
    String filter() default "";

}
