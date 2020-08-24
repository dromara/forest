package com.dtflys.forest.file;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.multipart.ForestMultipart;
import com.dtflys.forest.multipart.ForestMultipartFactory;
import com.dtflys.forest.utils.StringUtils;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SpringResource extends ForestMultipart<Resource> {

    private Resource resource;

    @Override
    public void setData(Resource data) {
        this.resource = data;
    }

    @Override
    public String getOriginalFileName() {
        if (StringUtils.isNotBlank(fileName)) {
            return fileName;
        }
        return resource.getFilename();
    }

    @Override
    public InputStream getInputStream() {
        try {
            return resource.getInputStream();
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
    }

    @Override
    public long getSize() {
        try {
            return resource.contentLength();
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
    }

    @Override
    public boolean isFile() {
        return true;
    }

    @Override
    public File getFile() {
        try {
            return resource.getFile();
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
    }
}
