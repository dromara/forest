package org.dromara.forest.http.body;

import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.mapping.MappingParameter;
import org.dromara.forest.utils.ForestDataType;
import org.dromara.forest.utils.RequestNameValue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class ByteArrayBodyItem extends BinaryBodyItem implements SupportFormUrlEncoded {

    private byte[] byteArray;

    public ByteArrayBodyItem(byte[] byteArray) {
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
    public ByteArrayBodyItem clone() {
        ByteArrayBodyItem newBody = new ByteArrayBodyItem(byteArray);
        newBody.setDefaultValue(getDefaultValue());
        return newBody;
    }

}
