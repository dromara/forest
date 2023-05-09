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

import org.dromara.forest.converter.ConvertOptions;
import org.dromara.forest.converter.ForestConverter;
import org.dromara.forest.converter.ForestEncoder;
import org.dromara.forest.http.ForestBody;
import org.dromara.forest.http.ForestRequestBody;
import org.dromara.forest.http.body.ObjectRequestBody;

import java.nio.charset.Charset;

public interface ForestProtobufConverter extends ForestConverter<byte[]>, ForestEncoder {

    byte[] convertToByte(Object source);

    @Override
    default String encodeToString(Object obj) {
        return null;
    }

    @Override
    default byte[] encodeRequestBody(final ForestBody body, final Charset charset, final ConvertOptions options) {
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
