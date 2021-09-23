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
     * request headers:
     * <p>    use the key-value format: key: value
     *     <pre>
     *         &#64;Headers("Content-Type: application/json")
     *     </pre>
     *     multiple headers
     *     <pre>
     *         &#64;Headers({
     *            "Content-Type: application/json",
     *            "Accept: text/plain"
     *         })
     *     </pre>
     *     variables and parameters
     *     <pre>
     *         &#64;Headers({"Accept: ${value}"})
     *     </pre>
     * @return headers
     */
    String[] value();


}
