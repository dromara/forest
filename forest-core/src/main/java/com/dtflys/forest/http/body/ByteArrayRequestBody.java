package com.dtflys.forest.http.body;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestBody;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.RequestNameValue;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class ByteArrayRequestBody extends BinaryRequestBody implements SupportFormUrlEncoded {

    private byte[] byteArray;

    public ByteArrayRequestBody(byte[] byteArray) {
        this.byteArray = byteArray;
    }

    public void setByteArray(byte[] byteArray) {
        this.byteArray = byteArray;
    }

    @Override
    public byte[] getByteArray() {
        return byteArray;
    }

    @Override
    InputStream getInputStream() {
        return new ByteArrayInputStream(byteArray);
    }

    @Override
    public ForestDataType getDefaultBodyType() {
        return ForestDataType.BINARY;
    }


    @Override
    public List<RequestNameValue> getNameValueList(ForestRequest request) {
        String str = new String(byteArray);
        List<RequestNameValue> nameValueList = new LinkedList<>();
        nameValueList.add(new RequestNameValue(str, MappingParameter.TARGET_BODY));
        return nameValueList;
    }

    @Override
    public ByteArrayRequestBody clone() {
        ByteArrayRequestBody newBody = new ByteArrayRequestBody(byteArray);
        newBody.setDefaultValue(getDefaultValue());
        return newBody;
    }

    @Override
    public String toString() {
        return new String(byteArray, StandardCharsets.UTF_8);
    }
}
