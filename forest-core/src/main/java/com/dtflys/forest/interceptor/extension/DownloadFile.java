package com.dtflys.forest.interceptor.extension;

import com.dtflys.forest.annotation.InterceptorClass;

import java.lang.annotation.*;

@Documented
@InterceptorClass(DownloadInterceptor.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DownloadFile {
    /**
     * The directory path of file you want to save
     * @return
     */
    String dir();

    /**
     * The file name you want to save
     * @return
     */
    String filename() default "";
}
