package org.dromara.forest.http.body;

import org.dromara.forest.multipart.ForestMultipart;
import org.dromara.forest.utils.ForestDataType;

import java.io.InputStream;

public class MultipartRequestBody extends BinaryRequestBody {

    private ForestMultipart multipart;

    public MultipartRequestBody(ForestMultipart multipart) {
        this.multipart = multipart;
    }

    public ForestMultipart getMultipart() {
        return multipart;
    }

    public void setMultipart(ForestMultipart multipart) {
        this.multipart = multipart;
    }

    @Override
    public byte[] getByteArray() {
        return multipart.getBytes();
    }

    @Override
    InputStream getInputStream() {
        return multipart.getInputStream();
    }

    @Override
    public ForestDataType getDefaultBodyType() {
        return ForestDataType.BINARY;
    }

    @Override
    public MultipartRequestBody clone() {
        final MultipartRequestBody newBody = new MultipartRequestBody(multipart);
        newBody.setDefaultValue(getDefaultValue());
        return newBody;
    }
}
