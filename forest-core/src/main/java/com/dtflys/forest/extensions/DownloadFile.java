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

    /**
     * 目录地址，即文件下载的目标目录的地址
     *
     * @return 目录地址
     */
    String dir();

    /**
     * 文件名，即文件下载完成后保存的文件名
     *
     * @return 文件名
     */
    String filename() default "";
}
