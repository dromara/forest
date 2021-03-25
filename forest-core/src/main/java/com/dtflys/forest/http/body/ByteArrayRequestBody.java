package com.dtflys.forest.http.body;

import com.dtflys.forest.http.ForestRequestBody;

public class ByteArrayRequestBody extends ForestRequestBody {

    private byte[] byteArray;

    public ByteArrayRequestBody(byte[] byteArray) {
        super(BodyType.BYTE_ARRAY);
        this.byteArray = byteArray;
    }

    public void setByteArray(byte[] byteArray) {
        this.byteArray = byteArray;
    }

    @Override
    public byte[] getByteArray() {
        return byteArray;
    }
}
