package com.dtflys.forest.annotation;

import com.dtflys.forest.lifecycles.parameter.DataFileLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 文件参数注解，在上传文件内容时使用，被该注解修饰的方法参数会被表示为一个文件
 * <p>
 * 该注解可以修饰一下几种类型的参数：
 *
 *     (1) 字符串类型：
 *
 *     upload(@DataFile("file") String filePath);
 *
 *     当该注解修饰的参数为字符串类型时，此参数表示为要上传的文件的路径。
 *     此时该注解的 fileName 参数可以省略。如若省略fileName，上传的文件名默认为文件路径中对应的文件名。
 *
 *     (2) File 类型对象：
 *
 *     upload(@DataFile("file") File file);
 *
 *     当该注解修饰的参数为File类型对象时，此参数表示为要上传的文件对象
 *     此时该注解的 fileName 参数可以省略。如若省略fileName，上传的文件名默认为File对象中对应的文件名。
 *
 *     (3) byte数组：
 *
 *     upload(@DataFile(value = "file", fileName = "xxx.jpg") byte[] bytes);
 *
 *     当该注解修饰的参数为byte数组时，此参数表示为要上传的文件二进制字节数组
 *     此时该注解的 fileName 参数不能省略，必须指定要上传的文件名。
 *
 *     (4) InputStream 对象：
 *
 *     upload(@DataFile(value = "file", fileName = "xxx.jpg") InputStream in);
 *
 *     当该注解修饰的参数为InputStream对象时，此参数表示为要上传的文件流数据流
 *     此时该注解的 fileName 参数不能省略，必须指定要上传的文件名。
 *
 *     (5) Spring Web MVC 中的 MultipartFile 对象：
 *
 *     upload(@DataFile(value = "file") MultipartFile multipartFile);
 *
 *     当该注解修饰的参数为MultipartFile对象时，此参数表示为要上传的文件对象。
 *     此时该注解的 fileName 参数可以省略。如若省略fileName，上传的文件名默认为MultipartFile对象中对应的文件名。
 *
 *     (6) Spring 的 Resource 对象：
 *
 *     upload(@DataFile(value = "file") Resource resource);
 *
 *     当该注解修饰的参数为Resource对象时，此参数表示为要上传的文件对象。
 *     此时该注解的 fileName 参数可以省略。如若省略fileName，上传的文件名默认为Resource对象中对应的文件名。
 *
 * <p/>
 *
 * @author gongjun
 * @since 2020-07-26 16:40
 */
@Documented
@ParamLifeCycle(DataFileLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface DataFile {

    /**
     * Multipart类型请求体中，要上传的文件所对应的参数名
     * @return
     */
    String value();

    /**
     * 要上传的文件的目标文件名（可省略）
     * @return
     */
    String fileName() default "";
}
