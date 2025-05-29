package com.dtflys.forest.test.http.address;

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
@Address(host = "localhost", port = "{port}", basePath = "abc")
@Headers({"Accept: text/plain"})
public @interface MyClient {
}
