package com.dtflys.forest.multipart;

import com.dtflys.forest.exceptions.ForestNoFileNameException;
import com.dtflys.forest.utils.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

public class ByteArrayMultipart extends ForestMultipart<byte[], ByteArrayMultipart> {

    private byte[] bytes;

    @Override
    public String getOriginalFileName() {
        if (StringUtils.isBlank(fileName)) {
            throw new ForestNoFileNameException(byte[].class);
        }
        return fileName;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public ByteArrayMultipart setData(byte[] data) {
        this.bytes = data;
        return this;
    }


    @Override
    public InputStream getInputStream() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        return byteArrayInputStream;
    }

    @Override
    public long getSize() {
        return bytes.length;
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
