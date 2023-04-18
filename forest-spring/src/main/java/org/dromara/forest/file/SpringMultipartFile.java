package org.dromara.forest.file;

import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.multipart.ForestMultipart;
import org.dromara.forest.utils.StringUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SpringMultipartFile extends ForestMultipart<MultipartFile, SpringMultipartFile> {

    private MultipartFile multipartFile;

    @Override
    public SpringMultipartFile setData(MultipartFile data) {
        this.multipartFile = data;
        return this;
    }

    @Override
    public String getOriginalFileName() {
        if (StringUtil.isNotBlank(fileName)) {
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
