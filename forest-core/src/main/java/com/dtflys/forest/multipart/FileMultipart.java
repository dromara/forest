package com.dtflys.forest.multipart;

import com.dtflys.forest.exceptions.ForestFileNotFoundException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.utils.StringUtils;

import java.io.*;

public class FileMultipart extends ForestMultipart<File> {

    private File file;


    @Override
    public String getOriginalFileName() {
        if (StringUtils.isNotBlank(fileName)) {
            return fileName;
        }
        return file.getName();
    }


    @Override
    public void setData(File data) {
        this.file = data;
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

    @Override
    public boolean isFile() {
        return true;
    }

    @Override
    public File getFile() {
        return file;
    }

}
