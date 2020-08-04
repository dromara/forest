package com.dtflys.forest.interceptor.extension;

import com.dtflys.forest.annotation.InterceptorTag;

import java.lang.annotation.*;

@Documented
@InterceptorTag(DownloadInterceptor.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DownloadFile {
    String value();
}
