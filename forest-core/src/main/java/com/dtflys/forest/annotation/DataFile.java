package com.dtflys.forest.annotation;

import com.dtflys.forest.lifecycles.parameter.DataFileLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author gongjun
 * @since 2020-07-26
 */
@Documented
@ParamLifeCycle(DataFileLifeCycle.class)
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
