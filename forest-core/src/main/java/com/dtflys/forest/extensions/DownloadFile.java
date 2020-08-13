package com.dtflys.forest.extensions;

import com.dtflys.forest.annotation.RequestAttributes;
import com.dtflys.forest.annotation.LifeCycle;
import com.dtflys.forest.lifecycles.DownloadLifeCycle;

import java.lang.annotation.*;

@Documented
@LifeCycle(DownloadLifeCycle.class)
@RequestAttributes
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
