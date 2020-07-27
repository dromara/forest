package com.dtflys.forest.multipart;

import com.dtflys.forest.exceptions.ForestFileNotFoundException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.utils.StringUtils;

import java.io.*;

public class FilePathMultipart implements ForestMultipart {

    private final String name;
    private final String fileName;
    private final String filePath;
    private final String contentType;

    public FilePathMultipart(String name, String fileName, String filePath, String contentType) {
        this.name = name;
        this.fileName = fileName;
        this.filePath = filePath;
        this.contentType = contentType;
    }


    @Override
    public String getName() {
        return name;
    }

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
