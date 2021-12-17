package com.dtflys.forest.multipart;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Forest对上传下载的文件、流、二进制数值等内容的封装抽象类
 * 提供针对用于上传下载等用途资源的公共接口
 *
 * @param <T> 文件数据类型, 可以有 {@link File}, {@link InputStream}, {@link String} 以及 {@code byte[]} 这几种基本类型
 * @author gongjun [dt_flys@hotmail.com]
 * @since 2020-08-02 15:01
 * @see ByteArrayMultipart
 * @see FileMultipart
 * @see FilePathMultipart
 * @see InputStreamMultipart
 */
public abstract class ForestMultipart<T, SELF extends ForestMultipart<T, SELF>> {

    protected String name;

    protected String fileName;

    protected String contentType;


    public String getName() {
        return name;
    }

    public SELF setName(String name) {
        this.name = name;
        return (SELF) this;
    }

    public SELF setFileName(String fileName) {
        this.fileName = fileName;
        return (SELF) this;
    }

    public String getContentType() {
        return contentType;
    }

    public SELF setContentType(String contentType) {
        this.contentType = contentType;
        return (SELF) this;
    }

    public abstract SELF setData(T data);

    public abstract String getOriginalFileName();

    public abstract InputStream getInputStream();

    public abstract long getSize();

    public abstract boolean isFile();

    public abstract File getFile();

    public byte[] getBytes() {
        InputStream inputStream = getInputStream();
        try {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
    }

}
