package com.dtflys.forest.file;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.multipart.ForestMultipart;
import com.dtflys.forest.utils.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SpringMultipartFile extends ForestMultipart<MultipartFile> {

    private MultipartFile multipartFile;

    @Override
    public void setData(MultipartFile data) {
        this.multipartFile = data;
    }

    @Override
    public String getOriginalFileName() {
        if (StringUtils.isNotBlank(fileName)) {
            return fileName;
        }
        return multipartFile.getOriginalFilename();
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
    public long getSize() {
        return multipartFile.getSize();
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public File getFile() {
        throw new ForestRuntimeException("[Forest] SpringMultipartFile instances are not files");
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
