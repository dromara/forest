package org.dromara.forest.http.body;

import org.dromara.forest.multipart.ForestMultipart;
import org.dromara.forest.utils.ForestDataType;

import java.io.InputStream;

public class MultipartBodyItem extends BinaryBodyItem {

    private ForestMultipart multipart;

    public MultipartBodyItem(ForestMultipart multipart) {
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
    public MultipartBodyItem clone() {
        MultipartBodyItem newBody = new MultipartBodyItem(multipart);
        newBody.setDefaultValue(getDefaultValue());
        return newBody;
    }
}
