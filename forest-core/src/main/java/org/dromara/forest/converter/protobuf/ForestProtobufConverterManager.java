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

import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.utils.ReflectUtils;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * JSON转换器选择策略
 *
 * @author YAKAX
 * @since 2020/12/18
 **/
public class ForestProtobufConverterManager implements Serializable {

    private static ForestProtobufConverterManager instance;
    private final static String PROTOBUF_CONVERTER_CLASS = "org.dromara.forest.converter.protobuf.ForestGoogleProtobufConverter";
    private final static String PROTOBUF_MESSAGE_CLASS = "com.google.protobuf.Message";

    private ForestProtobufConverter forestProtobufConverter;

    private volatile Class messageClass;

    private volatile Boolean supportProtobuf = null;

    public static ForestProtobufConverterManager getInstance() {
        if (instance != null) {
            return instance;
        }
        instance = new ForestProtobufConverterManager();
        return instance;
    }

    public boolean checkSupportProtobuf() {
        if (supportProtobuf != null) {
            return supportProtobuf;
//            throw new ForestRuntimeException("Protobuf is not supported.");
        }
        try {
            Class.forName("com.google.protobuf.Parser");
        } catch (Throwable ignored) {
            supportProtobuf = false;
            return false;
        }
        supportProtobuf = true;
        return true;
    }

    private Class<?> getMessageClass() {
        if (!checkSupportProtobuf()) {
            return null;
        }
        if (messageClass == null) {
            try {
                messageClass = Class.forName(PROTOBUF_MESSAGE_CLASS);
            } catch (ClassNotFoundException e) {
            }
        }
        return messageClass;
    }

    public boolean isProtobufMessageClass(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        if (!checkSupportProtobuf()) {
            return false;
        }
        final Class<?> messageClazz = getMessageClass();
        return messageClazz.isAssignableFrom(clazz);
    }

    public boolean isProtobufMessageType(Type type) {
        if (type == null) {
            return false;
        }
        Class<?> clazz = ReflectUtils.toClass(type);
        return isProtobufMessageClass(clazz);
    }

    public ForestProtobufConverter getForestProtobufConverter() {
        if (forestProtobufConverter == null) {
            synchronized (this) {
                if (forestProtobufConverter == null) {
                    if (checkSupportProtobuf()) {
                        try {
                            Class clazz = Class.forName(PROTOBUF_CONVERTER_CLASS);
                            forestProtobufConverter = (ForestProtobufConverter) clazz.newInstance();
                        } catch (Throwable th) {
                            throw new ForestRuntimeException("forestProtobufConverter create exception", th);
                        }
                    }
                }
            }
        }
        return forestProtobufConverter;
    }
}
