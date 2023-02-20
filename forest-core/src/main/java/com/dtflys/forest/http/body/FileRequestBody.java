package com.dtflys.forest.http.body;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.utils.ForestDataType;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FileRequestBody extends BinaryRequestBody {

    private File file;

    public FileRequestBody(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public byte[] getByteArray() {
        try {
            return FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
    }

    @Override
    InputStream getInputStream() {
        try {
            return FileUtils.openInputStream(file);
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
    }

    @Override
    public ForestDataType getDefaultBodyType() {
        return ForestDataType.BINARY;
    }
}
