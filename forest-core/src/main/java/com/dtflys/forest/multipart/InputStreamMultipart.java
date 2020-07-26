package com.dtflys.forest.multipart;

import java.io.InputStream;

public class InputStreamMultipart implements ForestMultipart {
    private final String name;
    private final String fileName;
    private final InputStream inputStream;
    private final String contentType;

    public InputStreamMultipart(String name, String fileName, InputStream inputStream, String contentType) {
        this.name = name;
        this.fileName = fileName;
        this.inputStream = inputStream;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFileName() {
        return fileName;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

}
