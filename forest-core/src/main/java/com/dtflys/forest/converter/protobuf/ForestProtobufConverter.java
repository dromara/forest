package com.dtflys.forest.converter.protobuf;

import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.auto.DefaultAutoConverter;
import com.dtflys.forest.exceptions.ForestConvertException;
import com.dtflys.forest.utils.ForestDataType;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import scala.annotation.meta.field;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
 * Protobuf转换器
 *
 * @author YAKAX
 * @since 2020/12/18
 **/
public class ForestProtobufConverter implements ForestConverter<byte[]> {

    @Override
    public <T> T convertToJavaObject(byte[] source, Type targetType) {
        Class<?> c = (Class<?>) targetType;
        Parser<T> parser;
        try {
            // 转换器 都会有parser方法
            Method method = c.getDeclaredMethod("parser");
            //noinspection unchecked
            parser = (Parser<T>) method.invoke(null);
            return parser.parseFrom(source);
        } catch (ReflectiveOperationException | InvalidProtocolBufferException e) {
            throw new ForestConvertException(this, e);
        }
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Class<T> targetType) {
        try {
            Method method = targetType.getDeclaredMethod("parser");
            //noinspection unchecked
            Parser<T> parser = (Parser<T>) method.invoke(null);
            return parser.parseFrom(source);
        } catch (ReflectiveOperationException | InvalidProtocolBufferException e) {
            throw new ForestConvertException(this, e);
        }
    }

    public byte[] convertToByte(Object source) {
        Class<?> aClass = source.getClass();
        if (!Message.class.isAssignableFrom(aClass)) {
            return new byte[]{};
        }
        Message message = (Message) source;
        return message.toByteArray();
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Class<T> targetType, Charset charset) {
        return this.convertToJavaObject(source, targetType);
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Type targetType, Charset charset) {
        return this.convertToJavaObject(source, targetType);
    }

    @Override
    public ForestDataType getDataType() {
        return ForestDataType.PROTOBUF;
    }


}
