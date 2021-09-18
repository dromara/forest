package com.dtflys.forest.multipart;

import com.dtflys.forest.exceptions.ForestNoFileNameException;
import com.dtflys.forest.utils.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class InputStreamMultipart extends ForestMultipart<InputStream, InputStreamMultipart> {

    private InputStream inputStream;

    @Override
    public String getOriginalFileName() {
        if (StringUtils.isBlank(fileName)) {
            throw new ForestNoFileNameException(inputStream.getClass());
        }
        return fileName;
    }


    @Override
    public InputStreamMultipart setData(InputStream data) {
        this.inputStream = data;
        return this;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public long getSize() {
        return -1;
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public File getFile() {
        return null;
    }

}
