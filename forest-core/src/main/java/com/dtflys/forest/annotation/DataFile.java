package com.dtflys.forest.annotation;

import java.lang.annotation.*;

/**
 * @author gongjun
 * @since 2020-07-26
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface DataFile {

    /**
     * The parameter name in request
     * @return
     */
    String value();

    /**
     * The name of file to upload (Optional)
     * @return
     */
    String fileName() default "";
}
