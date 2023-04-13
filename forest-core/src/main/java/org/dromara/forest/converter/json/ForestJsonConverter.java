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

package org.dromara.forest.converter.json;

import org.dromara.forest.converter.ConvertOptions;
import org.dromara.forest.converter.ForestConverter;
import org.dromara.forest.converter.ForestEncoder;
import org.dromara.forest.http.ForestBody;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestRequestBody;
import org.dromara.forest.http.Lazy;
import org.dromara.forest.http.body.BinaryRequestBody;
import org.dromara.forest.http.body.ByteArrayRequestBody;
import org.dromara.forest.http.body.NameValueRequestBody;
import org.dromara.forest.http.body.ObjectRequestBody;
import org.dromara.forest.http.body.StringRequestBody;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Forest的JSON数据转换接口
 *
 * @author gongjun
 * @since 2016-05-30
 */
public interface ForestJsonConverter extends ForestConverter<String>, ForestEncoder {


    /**
     * 将源对象转换为Map对象
     *
     * @param obj 源对象
     * @param request 请求对象
     * @param options 转换选项
     * @return 转换后的Map对象
     * @since 1.5.29
     */
    Map<String, Object> convertObjectToMap(Object obj, ForestRequest request, ConvertOptions options);

    /**
     * 将源对象转换为Map对象
     *
     * @param obj  源对象
     * @param request 请求对象
     * @return 转换后的Map对象
     */
    default Map<String, Object> convertObjectToMap(Object obj, ForestRequest request) {
        return convertObjectToMap(obj, request, ConvertOptions.defaultOptions());
    }


    default Map<String, Object> convertObjectToMap(Object obj) {
        return convertObjectToMap(obj, null, ConvertOptions.defaultOptions());
    }


    /**
     * 设置日期格式
     * @param format 日期格式化模板字符
     */
    void setDateFormat(String format);

    /**
     * 获取日期格式
     * @return 日期格式化模板字符
     */
    String getDateFormat();

    @Override
    default byte[] encodeRequestBody(final ForestBody body, final Charset charset, final ConvertOptions options) {
        final Charset cs = charset != null ? charset : StandardCharsets.UTF_8;
        List<ForestRequestBody> bodyList = new LinkedList<>(body);
        final ForestRequest request = body.getRequest();
        if (!bodyList.isEmpty()) {
            Object toJsonObj = bodyList;
            Map<String, Object> jsonMap = null;
            List jsonArray = null;
            if (bodyList.size() == 1 && (
                    bodyList.get(0) instanceof StringRequestBody ||
                    bodyList.get(0) instanceof BinaryRequestBody
            )) {
                return bodyList.get(0).getByteArray();
            }
            for (ForestRequestBody bodyItem : bodyList) {
                if (bodyItem instanceof NameValueRequestBody) {
                    if (jsonMap == null) {
                        jsonMap = new LinkedHashMap<>(bodyList.size());
                    }
                    NameValueRequestBody nameValueItem = (NameValueRequestBody) bodyItem;
                    String name = nameValueItem.getName();
                    if (options != null && options.shouldExclude(name)) {
                        continue;
                    }
                    Object value = nameValueItem.getOriginalValue();
                    if (Lazy.isEvaluatingLazyValue(value, request)) {
                        continue;
                    }
                    if (options != null) {
                        value = options.getValue(value, request);
                        if (options.shouldIgnore(value)) {
                            continue;
                        }
                    }
                    jsonMap.put(name, value);
                } else if (bodyItem instanceof StringRequestBody) {
                    String content = bodyItem.toString();
                    Map subMap = this.convertObjectToMap(content, request);
                    if (subMap != null) {
                        if (jsonMap == null) {
                            jsonMap = new LinkedHashMap<>(bodyList.size());
                        }
                        jsonMap.putAll(subMap);
                    } else {
                        if (jsonArray == null) {
                            jsonArray = new LinkedList<>();
                        }
                        jsonArray.add(content);
                    }
                } else if (bodyItem instanceof ObjectRequestBody) {
                    Object obj = ((ObjectRequestBody) bodyItem).getObject();
                    if (obj == null) {
                        continue;
                    }
                    if (obj instanceof List) {
                        if (jsonArray == null) {
                            jsonArray = new LinkedList();
                        }
                        jsonArray.addAll((List) obj);
                    } else {
                        Map subMap = this.convertObjectToMap(obj, request, options);
                        if (subMap == null) {
                            continue;
                        }
                        if (jsonMap == null) {
                            jsonMap = new LinkedHashMap<>(bodyList.size());
                        }
                        jsonMap.putAll(subMap);
                    }
                }
            }
            if (jsonMap != null) {
                toJsonObj = jsonMap;
            } else if (jsonArray != null) {
                toJsonObj = jsonArray;
            }
            String text = null;
            if (toJsonObj instanceof CharSequence || toJsonObj instanceof StringRequestBody) {
                text = toJsonObj.toString();
                return text.getBytes(cs);
            } else if (toJsonObj instanceof ObjectRequestBody) {
                text = this.encodeToString(((ObjectRequestBody) toJsonObj).getObject());
                return text.getBytes(cs);
            } else if (toJsonObj instanceof NameValueRequestBody) {
                Map<String, Object> subMap = new HashMap<>(1);
                subMap.put(((NameValueRequestBody) toJsonObj).getName(), ((NameValueRequestBody) toJsonObj).getValue());
                text = this.encodeToString(subMap);
                return text.getBytes(cs);
            } else if (toJsonObj instanceof ByteArrayRequestBody) {
                byte[] bytes = ((ByteArrayRequestBody) toJsonObj).getByteArray();
                return bytes;
            } else {
                text = this.encodeToString(toJsonObj);
                return text.getBytes(cs);
            }
        }
        return new byte[0];
    }
}
