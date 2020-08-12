package com.dtflys.forest.annotation;

import java.lang.annotation.*;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-12 22:26
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Header {

    /**
     * Request header name
     * @return
     */
    String value();

}
