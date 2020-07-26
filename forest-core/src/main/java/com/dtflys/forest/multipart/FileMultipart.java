package com.dtflys.forest.multipart;

import com.dtflys.forest.exceptions.ForestFileNotFoundException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.utils.StringUtils;

import java.io.*;

public class FileMultipart implements ForestMultipart {

    private final String name;
    private final String fileName;
    private final File file;
    private final String contentType;

    public FileMultipart(String name, String fileName, File file, String contentType) {
        this.name = name;
        this.fileName = fileName;
        this.file = file;
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
        return file.getName();
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public InputStream getInputStream() {
        if (!file.exists()) {
            throw new ForestFileNotFoundException(file.getAbsolutePath());
        }
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new ForestRuntimeException(e);
        }
    }

}
