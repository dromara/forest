package com.dtflys.forest.multipart;

import com.dtflys.forest.exceptions.ForestFileNotFoundException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.utils.StringUtils;

import java.io.*;

public class FilePathMultipart extends ForestMultipart<String> {

    private String filePath;


    @Override
    public String getOriginalFileName() {
        if (StringUtils.isNotBlank(fileName)) {
            return fileName;
        }
        String[] strs = filePath.split("(/|\\\\)");
        return strs[strs.length - 1];
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public void setData(String data) {
        this.filePath = data;
    }

    @Override
    public InputStream getInputStream() {
        File file = getFile();
        if (!file.exists()) {
            throw new ForestFileNotFoundException(filePath);
        }
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new ForestRuntimeException(e);
        }
    }

    @Override
    public boolean isFile() {
        return true;
    }

    @Override
    public File getFile() {
        File file = new File(filePath);
        return file;
    }
}
