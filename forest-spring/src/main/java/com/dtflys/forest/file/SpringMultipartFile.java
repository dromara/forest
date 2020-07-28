package com.dtflys.forest.file;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.multipart.ForestMultipart;
import com.dtflys.forest.utils.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SpringMultipartFile implements ForestMultipart {

    private final String name;
    private final String fileName;
    private final MultipartFile multipartFile;
    private final String contentType;

    public SpringMultipartFile(String name, String fileName, MultipartFile multipartFile, String contentType) {
        this.name = name;
        this.fileName = fileName;
        this.multipartFile = multipartFile;
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
        return multipartFile.getOriginalFilename();
    }

    @Override
    public String getContentType() {
        if (StringUtils.isNotBlank(contentType)) {
            return contentType;
        }
        return multipartFile.getContentType();
    }

    @Override
    public InputStream getInputStream() {
        try {
            return multipartFile.getInputStream();
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
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
        try {
            return multipartFile.getBytes();
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
    }
}
