package com.dtflys.forest.converter.protobuf;

import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.ForestEncoder;
import com.dtflys.forest.http.ForestBody;
import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.http.body.ObjectRequestBody;

import java.nio.charset.Charset;

public interface ForestProtobufConverter extends ForestConverter<byte[]>, ForestEncoder {

    byte[] convertToByte(Object source);

    @Override
    default String encodeToString(Object obj) {
        return null;
    }

    @Override
    default byte[] encodeRequestBody(ForestBody body, Charset charset) {
        ForestProtobufConverterManager protobufConverterManager = ForestProtobufConverterManager.getInstance();
        Object protobufObj = null;
        for (ForestRequestBody bodyItem : body) {
            if (bodyItem instanceof ObjectRequestBody) {
                Object obj = ((ObjectRequestBody) bodyItem).getObject();
                if (protobufConverterManager.isProtobufMessageClass(obj.getClass())) {
                    protobufObj = obj;
                    break;
                }
            }
        }
        if (protobufObj != null) {
            return this.convertToByte(protobufObj);
        }
        return new byte[0];
    }
}
