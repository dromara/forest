package com.dtflys.forest.multipart;

import com.dtflys.forest.exceptions.ForestNoFileNameException;
import com.dtflys.forest.utils.StringUtils;

import java.io.File;
import java.io.InputStream;

public class InputStreamMultipart extends ForestMultipart<InputStream> {

    private InputStream inputStream;

    @Override
    public String getOriginalFileName() {
        if (StringUtils.isBlank(fileName)) {
            throw new ForestNoFileNameException(inputStream.getClass());
        }
        return fileName;
    }


    @Override
    public void setData(InputStream data) {
        this.inputStream = data;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
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
