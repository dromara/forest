package org.dromara.forest.http.body;

import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.utils.ForestDataType;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FileBodyItem extends BinaryBodyItem {

    private File file;

    public FileBodyItem(File file) {
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

    @Override
    public FileBodyItem clone() {
        FileBodyItem newBody = new FileBodyItem(file);
        newBody.setDefaultValue(getDefaultValue());
        return newBody;
    }
}
