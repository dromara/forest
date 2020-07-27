package com.dtflys.forest.multipart;

import com.dtflys.forest.exceptions.ForestRuntimeException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface ForestMultipart {

    int BUFFER_SIZE = 1024;

    String getName();

    String getOriginalFileName();

    String getContentType();

    InputStream getInputStream();

    boolean isFile();

    File getFile();

    default byte[] getBytes() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        InputStream inputStream = getInputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int len = 0;
        try {
            while ((len = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            byteArrayOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
    }

}
