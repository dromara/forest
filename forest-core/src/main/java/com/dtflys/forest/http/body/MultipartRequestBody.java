package com.dtflys.forest.http.body;

import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.multipart.ForestMultipart;
import com.dtflys.forest.utils.ForestDataType;

public class MultipartRequestBody extends ForestRequestBody {

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
    public ForestDataType getDefaultBodyType() {
        return ForestDataType.BINARY;
    }
}
