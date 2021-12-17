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

package com.dtflys.forest.converter.json;

import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.ForestEncoder;
import com.dtflys.forest.http.ForestBody;
import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.http.body.ByteArrayRequestBody;
import com.dtflys.forest.http.body.NameValueRequestBody;
import com.dtflys.forest.http.body.ObjectRequestBody;
import com.dtflys.forest.http.body.StringRequestBody;

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
     * @param obj  源对象
     * @return 转换后的Map对象
     */
    Map<String, Object> convertObjectToMap(Object obj);

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
    default byte[] encodeRequestBody(ForestBody body, Charset charset) {
        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }
        List<ForestRequestBody> bodyList = new LinkedList(body);
        if (!bodyList.isEmpty()) {
            Object toJsonObj = bodyList;
            if (bodyList.size() == 1) {
                toJsonObj = bodyList.get(0);
            } else {
                Map<String, Object> jsonMap = null;
                List jsonArray = null;
                for (ForestRequestBody bodyItem : bodyList) {
                    if (bodyItem instanceof NameValueRequestBody) {
                        if (jsonMap == null) {
                            jsonMap = new LinkedHashMap<>(bodyList.size());
                        }
                        jsonMap.put(((NameValueRequestBody) bodyItem).getName(), ((NameValueRequestBody) bodyItem).getValue());
                    } else if (bodyItem instanceof StringRequestBody) {
                        String content = bodyItem.toString();
                        Map subMap = this.convertObjectToMap(content);
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
                            Map subMap = this.convertObjectToMap(obj);
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
            }
            String text = null;
            if (toJsonObj instanceof CharSequence || toJsonObj instanceof StringRequestBody) {
                text = toJsonObj.toString();
                return text.getBytes(charset);
            } else if (toJsonObj instanceof ObjectRequestBody) {
                text = this.encodeToString(((ObjectRequestBody) toJsonObj).getObject());
                return text.getBytes(charset);
            } else if (toJsonObj instanceof NameValueRequestBody) {
                Map<String, Object> subMap = new HashMap<>(1);
                subMap.put(((NameValueRequestBody) toJsonObj).getName(), ((NameValueRequestBody) toJsonObj).getValue());
                text = this.encodeToString(subMap);
                return text.getBytes(charset);
            } else if (toJsonObj instanceof ByteArrayRequestBody) {
                byte[] bytes = ((ByteArrayRequestBody) toJsonObj).getByteArray();
                return bytes;
            } else {
                text = this.encodeToString(toJsonObj);
                return text.getBytes(charset);
            }
        }
        return new byte[0];
    }
}
