package com.dtflys.forest.multipart;

import com.dtflys.forest.exceptions.ForestRuntimeException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Forest对上传下载的文件、流、二进制数值等内容的封装抽象类，
 * 提供针对用于上传下载等用途资源的公共接口</p>
 * @param <T>
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-02 15:01
 */
public abstract class ForestMultipart<T> {

    private int BUFFER_SIZE = 4096;

    protected String name;

    protected String fileName;

    protected String contentType;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public abstract void setData(T data);

    public abstract String getOriginalFileName();

    public abstract InputStream getInputStream();

    public abstract long getSize();

    public abstract boolean isFile();

    public abstract File getFile();

    public byte[] getBytes() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        InputStream inputStream = getInputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int len = 0;
        try {
            while ((len = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            byteArrayOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        } finally {
            try {
                inputStream.close();
                byteArrayOutputStream.close();
            } catch (IOException e) {
                throw new ForestRuntimeException(e);
            }
        }
    }

}
