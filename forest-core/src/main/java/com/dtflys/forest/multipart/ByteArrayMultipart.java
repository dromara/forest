package com.dtflys.forest.multipart;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

public class ByteArrayMultipart implements ForestMultipart {

    private final String name;

    private final String fileName;

    private final byte[] bytes;

    private final String contentType;

    public ByteArrayMultipart(String name, String fileName, byte[] bytes, String contentType) {
        this.name = name;
        this.fileName = fileName;
        this.bytes = bytes;
        this.contentType = contentType;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFileName() {
        return name;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public InputStream getInputStream() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        return byteArrayInputStream;
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public File getFile() {
        return null;
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }
}
