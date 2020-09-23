package com.dtflys.forest.extensions;

import com.dtflys.forest.annotation.RequestAttributes;
import com.dtflys.forest.annotation.MethodLifeCycle;
import com.dtflys.forest.lifecycles.file.DownloadLifeCycle;

import java.lang.annotation.*;

/**
 * 文件下载注解
 * <p>该注解的生命周期类为 {@link DownloadLifeCycle}</p>
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @see DownloadLifeCycle
 */
@Documented
@MethodLifeCycle(DownloadLifeCycle.class)
@RequestAttributes
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DownloadFile {

    /** The directory path of file you want to save */
    String dir();

    /** The file name you want to save */
    String filename() default "";
}
