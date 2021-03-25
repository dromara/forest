package com.dtflys.forest.http.body;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequestBody;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamRequestBody extends ForestRequestBody {

    private InputStream inputStream;

    public InputStreamRequestBody(InputStream inputStream) {
        super(BodyType.INPUT_STREAM);
        this.inputStream = inputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public byte[] getByteArray() {
        try {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
    }
}
