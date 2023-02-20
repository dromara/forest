package com.dtflys.forest.http.body;

import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.multipart.ForestMultipart;
import com.dtflys.forest.utils.ForestDataType;

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
}
