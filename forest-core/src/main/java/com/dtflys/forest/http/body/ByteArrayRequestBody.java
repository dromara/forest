package com.dtflys.forest.http.body;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.utils.RequestNameValue;

import java.util.LinkedList;
import java.util.List;

public class ByteArrayRequestBody extends ForestRequestBody implements SupportFormUrlEncoded {

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
    public List<RequestNameValue> getNameValueList(ForestConfiguration configuration) {
        String str = new String(byteArray);
        List<RequestNameValue> nameValueList = new LinkedList<>();
        nameValueList.add(new RequestNameValue(str, MappingParameter.TARGET_BODY));
        return nameValueList;
    }
}
