package com.dtflys.forest.converter.protobuf;

import com.dtflys.forest.converter.ForestConverter;

public interface ForestProtobufConverter extends ForestConverter<byte[]> {

    byte[] convertToByte(Object source);

}
