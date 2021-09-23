package com.dtflys.forest.annotation;

import com.dtflys.forest.lifecycles.method.HeadersLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@MethodLifeCycle(HeadersLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface Headers {

    /**
     * request headers: <br>
     *     use the key-value format: key: value <br>
     *     <pre>
     *         @Headers("Content-Type: application/json")
     *     </pre>
     *     multiple headers <br>
     *     <pre>
     *         @Headers({
     *            "Content-Type: application/json",
     *            "Accept: text/plain"
     *         })
     *     </pre>
     *     variables and parameters <br>
     *     <pre>
     *         @Headers({"Accept: ${value}"})
     *     </pre>
     */
    String[] value();


}
