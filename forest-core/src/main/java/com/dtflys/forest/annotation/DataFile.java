/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Jun Gong
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.dtflys.forest.annotation;

import com.dtflys.forest.lifecycles.parameter.DataFileLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 文件参数注解
 * <p>在上传文件内容时使用，被该注解修饰的方法参数会被表示为一个文件</p>
 * <p>该注解可以修饰一下几种类型的参数：</p>
 *
 *     (1) 字符串类型：<br><br>
 *
 *     <pre>
 *     void upload(@DataFile("file") String filePath);
 *     </pre>
 *
 *     当该注解修饰的参数为字符串类型时，此参数表示为要上传的文件的路径。
 *     此时该注解的 fileName 参数可以省略。如若省略fileName，上传的文件名默认为文件路径中对应的文件名。<br><br>
 *
 *     (2) File 类型对象：<br><br>
 *
 *     <pre>
 *     void upload(@DataFile("file") File file);
 *     </pre>
 *
 *     当该注解修饰的参数为File类型对象时，此参数表示为要上传的文件对象
 *     此时该注解的 fileName 参数可以省略。如若省略fileName，上传的文件名默认为File对象中对应的文件名。<br><br>
 *
 *     (3) byte数组：<br><br>
 *
 *     <pre>
 *     void upload(@DataFile(value = "file", fileName = "xxx.jpg") byte[] bytes);
 *     </pre>
 *
 *     当该注解修饰的参数为byte数组时，此参数表示为要上传的文件二进制字节数组
 *     此时该注解的 fileName 参数不能省略，必须指定要上传的文件名。<br><br>
 *
 *     (4) InputStream 对象：<br><br>
 *
 *     <pre>
 *     void upload(@DataFile(value = "file", fileName = "xxx.jpg") InputStream in);
 *     </pre>
 *
 *     当该注解修饰的参数为InputStream对象时，此参数表示为要上传的文件流数据流
 *     此时该注解的 fileName 参数不能省略，必须指定要上传的文件名。<br><br>
 *
 *     (5) Spring Web MVC 中的 MultipartFile 对象：<br><br>
 *
 *     <pre>
 *     void upload(@DataFile(value = "file") MultipartFile multipartFile);
 *     </pre>
 *
 *     当该注解修饰的参数为MultipartFile对象时，此参数表示为要上传的文件对象。
 *     此时该注解的 fileName 参数可以省略。如若省略fileName，上传的文件名默认为MultipartFile对象中对应的文件名。<br><br>
 *
 *     (6) Spring 的 Resource 对象：<br><br>
 *
 *     <pre>
 *     void upload(@DataFile(value = "file") Resource resource);
 *     </pre>
 *
 *     当该注解修饰的参数为Resource对象时，此参数表示为要上传的文件对象。
 *     此时该注解的 fileName 参数可以省略。如若省略fileName，上传的文件名默认为Resource对象中对应的文件名。
 *
 * @author gongjun
 * @since 2020-07-26 16:40
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
public @interface DataFile {

    /**
     * Multipart类型请求体中，要上传的文件所对应的参数名
     * @return 参数名
     */
    String value();


    /**
     * 要上传的文件的目标文件名（可省略）
     * @return 目标文件名
     */
    String fileName() default "";

    /**
     * 子项Content-Type
     * @return 子项Content-Type
     */
    String partContentType() default "";
}
