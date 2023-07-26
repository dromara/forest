package org.dromara.forest.http.body;

import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.http.ForestRequestBody;
import org.dromara.forest.utils.ForestDataType;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamRequestBody extends ForestRequestBody {

    private InputStream inputStream;

    public InputStreamRequestBody(InputStream inputStream) {
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

    @Override
    public ForestDataType getDefaultBodyType() {
        return ForestDataType.BINARY;
    }

    @Override
    public InputStreamRequestBody clone() {
        final InputStreamRequestBody newBody = new InputStreamRequestBody(inputStream);
        newBody.setDefaultValue(getDefaultValue());
        return newBody;
    }
}
