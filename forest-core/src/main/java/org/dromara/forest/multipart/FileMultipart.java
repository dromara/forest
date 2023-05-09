package org.dromara.forest.multipart;

import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.utils.StringUtils;
import org.apache.commons.io.FileUtils;

import java.io.*;

public class FileMultipart extends ForestMultipart<File, FileMultipart> {

    private File file;

    @Override
    public String getOriginalFileName() {
        if (StringUtils.isNotBlank(fileName)) {
            return fileName;
        }
        return file.getName();
    }


    @Override
    public FileMultipart setData(File data) {
        this.file = data;
        return this;
    }

    @Override
    public InputStream getInputStream() {
        try {
            return FileUtils.openInputStream(file);
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
    }

    @Override
    public long getSize() {
        return file.length();
    }

    @Override
    public boolean isFile() {
        return true;
    }

    @Override
    public File getFile() {
        return file;
    }

}
