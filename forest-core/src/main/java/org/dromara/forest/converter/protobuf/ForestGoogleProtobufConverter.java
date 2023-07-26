/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Jun Gong
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.dromara.forest.converter.protobuf;

import org.dromara.forest.exceptions.ForestConvertException;
import org.dromara.forest.utils.ForestDataType;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
 * Protobuf转换器
 *
 * @author YAKAX
 * @since 2020/12/18
 **/
public class ForestGoogleProtobufConverter implements ForestProtobufConverter {

    @Override
    public <T> T convertToJavaObject(byte[] source, Type targetType) {
        final Class<?> c = (Class<?>) targetType;
        try {
            // 转换器 都会有parser方法
            final Method method = c.getDeclaredMethod("parser");
            //noinspection unchecked
            final Parser<T> parser = (Parser<T>) method.invoke(null);
            return parser.parseFrom(source);
        } catch (ReflectiveOperationException | InvalidProtocolBufferException e) {
            throw new ForestConvertException(this, e);
        }
    }


    @Override
    public byte[] convertToByte(Object source) {
        final Class<?> aClass = source.getClass();
        if (!Message.class.isAssignableFrom(aClass)) {
            return new byte[]{};
        }
        final Message message = (Message) source;
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
