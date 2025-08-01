package com.dtflys.forest.springboot.test.customize;

import com.dtflys.forest.annotation.Headers;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Headers({
        "Accept: {myAccept}",
        "Foo: {foo}"
})
public @interface MyHeaders {
}
