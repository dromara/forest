package com.dtflys.forest.springboot3.test.customize;

import com.dtflys.forest.annotation.Headers;
import com.dtflys.forest.annotation.RequestAttributes;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@RequestAttributes
@Headers({
        "Accept: {myAccept}",
        "Foo: {foo}"
})
public @interface MyHeaders {
}
