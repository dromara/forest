package com.dtflys.forest.http.body;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequestBody;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class FileRequestBody extends ForestRequestBody {

    private File file;

    public FileRequestBody(File file) {
        super(BodyType.FILE);
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
}
