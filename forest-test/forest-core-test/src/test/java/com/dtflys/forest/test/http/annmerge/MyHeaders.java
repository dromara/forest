package com.dtflys.forest.test.http.annmerge;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.Headers;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Headers({
        "Accept: text/plain",
        "Content-Type: application/json"
})
@Address(port = "${port}")
public @interface MyHeaders {
}
