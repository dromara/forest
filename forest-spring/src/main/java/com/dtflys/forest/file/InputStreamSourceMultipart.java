package com.dtflys.forest.file;

import com.dtflys.forest.exceptions.ForestNoFileNameException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.multipart.ForestMultipart;
import com.dtflys.forest.utils.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class InputStreamSourceMultipart implements ForestMultipart {

    private final String name;
    private final String fileName;
    private final InputStreamSource inputStreamSource;
    private final String contentType;

    public InputStreamSourceMultipart(String name, String fileName, InputStreamSource inputStreamSource, String contentType) {
        this.name = name;
        this.fileName = fileName;
        this.inputStreamSource = inputStreamSource;
        this.contentType = contentType;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFileName() {
        if (StringUtils.isBlank(fileName)) {
            throw new ForestNoFileNameException(inputStreamSource.getClass());
        }
        return fileName;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public InputStream getInputStream() {
        try {
            return inputStreamSource.getInputStream();
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
    }

    @Override
    public boolean isFile() {
        if (inputStreamSource instanceof Resource) {
            return true;
        }
        return false;
    }

    @Override
    public File getFile() {
        if (inputStreamSource instanceof Resource) {
            try {
                return ((Resource) inputStreamSource).getFile();
            } catch (IOException e) {
                throw new ForestRuntimeException(e);
            }
        }
        return null;
    }
}
