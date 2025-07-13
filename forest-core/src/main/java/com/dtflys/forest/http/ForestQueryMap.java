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

package com.dtflys.forest.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.utils.StringUtils;
import com.dtflys.forest.utils.URLUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Forest请求Query参数Map
 * <p>该类负责批量管理在Forest请求中所有的请求Query参数</p>
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-08-11 12:45
 */
public class ForestQueryMap implements Map<String, Object> {

    final ForestRequest request;

    private final List<SimpleQueryParameter> queries;

    public ForestQueryMap(final ForestRequest request) {
        this.request = request;
        this.queries = new LinkedList<>();
    }

    public ForestRequest getRequest() {
        return request;
    }

    @Override
    public int size() {
        return queries.size();
    }

    @Override
    public boolean isEmpty() {
        return queries.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            return false;
        }
        final String name = key.toString();
        for (SimpleQueryParameter query : queries) {
            if (query.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        for (SimpleQueryParameter query : queries) {
            final Object queryVal = query.getValue();
            if (queryVal == null) {
                if (value == null) {
                    return true;
                }
                continue;
            }
            if (queryVal.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public SimpleQueryParameter getQuery(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        for (SimpleQueryParameter query : queries) {
            if (query.getName().equalsIgnoreCase(name)) {
                return query;
            }
        }
        return null;
    }

    public List<SimpleQueryParameter> getQueries(String name) {
        final List<SimpleQueryParameter> list = new LinkedList<>();
        if (StringUtils.isEmpty(name)) {
            return list;
        }
        for (SimpleQueryParameter query : queries) {
            if (query.getName().equalsIgnoreCase(name)) {
                list.add(query);
            }
        }
        return list;
    }

    @Override
    public Object get(Object key) {
        if (key == null) {
            return null;
        }
        final String name = key.toString();
        final SimpleQueryParameter query = getQuery(name);
        if (query == null) {
            return null;
        }
        return query.getValue();
    }

    public void addQuery(SimpleQueryParameter query) {
        query.queries = this;
        queries.add(query);
    }

    public void addAllQueries(ForestQueryMap queries) {
        if (queries != null) {
            this.queries.addAll(queries.queries);
        }
    }

    public void addQuery(String name, Object value) {
        addQuery(name, value, false, null);
    }

    public void addQuery(String name, Lazy value) {
        addQuery(name, value, false, null);
    }


    /**
     * 添加 Query 参数
     *
     * @param name 参数名
     * @param value 参数值
     * @param isUrlEncode 是否强制 UrlEncode
     * @param charset 编码字符集
     */
    public void addQuery(String name, Object value, boolean isUrlEncode, String charset) {
        if (value instanceof Collection) {
            addQuery(name, (Collection) value, isUrlEncode, charset);
        } else {
            queries.add(new SimpleQueryParameter(this, name, value, isUrlEncode, charset));
        }
    }

    /**
     * 添加集合类 Query 参数
     *
     * @param name 参数名
     * @param collection 集合对象
     * @since 1.5.4
     */
    public void addQuery(String name, Collection collection) {
        addQuery(name, collection, false, null);
    }


    /**
     * 添加集合类 Query 参数
     *
     * @param name 参数名
     * @param collection 集合对象
     * @param isUrlEncode 是否强制 UrlEncode
     * @param charset 编码字符集
     * @since 1.5.4
     */
    public void addQuery(String name, Collection collection, boolean isUrlEncode, String charset) {
        for (Object item : collection) {
            addQuery(name, item, isUrlEncode, charset);
        }
    }

    /**
     * 添加数组 Query 参数
     *
     * @param name 参数名
     * @param array 数组
     * @since 1.5.4
     */
    public void addQuery(String name, Object[] array) {
        addQuery(name, array, false, null);
    }


    /**
     * 添加数组 Query 参数
     *
     * @param name 参数名
     * @param array 数组
     * @param isUrlEncode 是否强制 UrlEncode
     * @param charset 编码字符集
     * @since 1.5.4
     */
    public void addQuery(String name, Object[] array, boolean isUrlEncode, String charset) {
        for (Object item : array) {
            addQuery(name, item, isUrlEncode, charset);
        }
    }


    /**
     * 添加 Map 类 Query 参数
     *
     * @param map Map对象
     * @since 1.5.4
     */
    public void addQuery(Map map) {
        for (Object key : map.keySet()) {
            Object value = map.get(key);
            addQuery(String.valueOf(key), value);
        }
    }


    /**
     * 添加 Map 类 Query 参数
     *
     * @param map Map对象
     * @param isUrlEncode 是否强制 UrlEncode
     * @param charset 编码字符集
     * @since 1.5.4
     */
    public void addQuery(Map map, boolean isUrlEncode, String charset) {
        if (map == null) {
            return;
        }
        for (Object key : map.keySet()) {
            final Object value = map.get(key);
            addQuery(String.valueOf(key), value, isUrlEncode, charset);
        }
    }

    /**
     * 添加带数组下标的 Query 参数
     *
     * @param name 参数名
     * @param collection 集合对象
     * @since 1.5.4
     */
    public void addArrayQuery(String name, Collection collection) {
        addArrayQuery(name, collection, false, null);
    }


    /**
     * 添加带数组下标的 Query 参数
     *
     * @param name 参数名
     * @param collection 集合对象
     * @param isUrlEncode 是否强制 UrlEncode
     * @param charset 编码字符集
     * @since 1.5.4
     */
    public void addArrayQuery(String name, Collection collection, boolean isUrlEncode, String charset) {
        for (Object item : collection) {
            addQuery(name + "[]", item, isUrlEncode, charset);
        }
    }

    /**
     * 添加带数组方括号的 Query 参数
     *
     * @param name 参数名
     * @param array 集合对象
     * @since 1.5.4
     */
    public void addArrayQuery(String name, Object[] array) {
        addArrayQuery(name, array, false, null);
    }


    /**
     * 添加带数组方括号的 Query 参数
     *
     * @param name 参数名
     * @param array 集合对象
     * @param isUrlEncode 是否强制 UrlEncode
     * @param charset 编码字符集
     * @since 1.5.4
     */
    public void addArrayQuery(String name, Object[] array, boolean isUrlEncode, String charset) {
        for (Object item : array) {
            addQuery(name + "[]", item, isUrlEncode, charset);
        }
    }



    @Override
    public Object put(String key, Object value) {
        SimpleQueryParameter query = getQuery(key);
        if (query != null) {
            query.setValue(value);
        } else {
            SimpleQueryParameter newQuery = new SimpleQueryParameter(this, key, value);
            addQuery(newQuery);
        }
        return value;
    }

    @Override
    public Object remove(Object key) {
        if (key == null) {
            return null;
        }
        final String name = key.toString();
        for (int i = queries.size() - 1; i >= 0; i--) {
            final SimpleQueryParameter query = queries.get(i);
            if (query.getName().equalsIgnoreCase(name)) {
                final SimpleQueryParameter removedQuery = queries.remove(i);
                return removedQuery.getValue();
            }
        }
        return null;
    }

    /**
     * 删除所有参数名为指定名称的Query参数
     *
     * @param name 参数名称
     * @return 被删除的参数
     */
    public List<SimpleQueryParameter> removeQueries(String name) {
        final List<SimpleQueryParameter> list = new LinkedList<>();
        if (name == null) {
            return list;
        }
        for (int i = queries.size() - 1; i >= 0; i--) {
            final SimpleQueryParameter query = queries.get(i);
            if (query.getName().equalsIgnoreCase(name)) {
                final SimpleQueryParameter removedQuery = queries.remove(i);
                list.add(0, removedQuery);
            }
        }
        return list;
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        if (m == null) {
            return;
        }
        for (Entry<? extends String, ?> entry : m.entrySet()) {
            final String name = entry.getKey();
            final Object value = entry.getValue();
            put(name, value);
        }
    }

    @Override
    public void clear() {
        queries.clear();
    }

    /**
     * 清除所有源自URL的Query参数
     */
    public void clearQueriesFromUrl() {
        final int len = queries.size();
        for (int i = len - 1; i >=0; i--) {
            final SimpleQueryParameter query = queries.get(i);
            if (query.isFromUrl()) {
                queries.remove(i);
            }
        }
    }

    @Override
    public Set<String> keySet() {
        final Set<String> set = new HashSet<>();
        for (SimpleQueryParameter query : queries) {
            set.add(query.getName());
        }
        return set;
    }

    @Override
    public Collection<Object> values() {
        final List<Object> list = new ArrayList<>();
        for (SimpleQueryParameter query : queries) {
            final Object val = query.getValue();
            if (val != null) {
                list.add(val);
            }
        }
        return list;
    }

    public List<SimpleQueryParameter> queryValues() {
        return queries;
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        final Set<Entry<String, Object>> set = new HashSet<>();
        for (SimpleQueryParameter query : queries) {
            final Entry<String, Object> entry = new Entry<String, Object>() {

                @Override
                public String getKey() {
                    return query.getName();
                }

                @Override
                public Object getValue() {
                    return query.getValue();
                }

                @Override
                public Object setValue(Object value) {
                    final Object oldValue = query.getValue();
                    query.setValue(value);
                    return oldValue;
                }
            };
            set.add(entry);
        }
        return set;
    }

    public String toQueryString() {
        final StringBuilder builder = new StringBuilder();
        final Iterator<SimpleQueryParameter> iterator = queries.iterator();
        final ForestJsonConverter jsonConverter = request.getConfiguration().getJsonConverter();
        final HttpBackend backend = request.getBackend();
        final boolean allowEncodeBraceInQueryValue =
                backend == null ? false : backend.isAllowEncodeBraceInQueryValue();
        int count = 0;
        while (iterator.hasNext()) {
            final SimpleQueryParameter query = iterator.next();
            if (query != null) {
                final String name = query.getName();
                final Object value = query.getOriginalValue();
                if (Lazy.isEvaluatingLazyValue(value, request)) {
                    continue;
                }
                if (count > 0) {
                    builder.append("&");
                }
                if (name != null) {
                    builder.append(name);
                    if (value != null) {
                        builder.append("=");
                    }
                }
                if (value != null) {
                    final Object evaluatedValue = query.getValue();
                    final String strValue = MappingTemplate.getParameterValue(jsonConverter, evaluatedValue);
                    if (strValue != null) {
                        String charset = query.getCharset();
                        if (StringUtils.isBlank(charset)) {
                            charset = request.getCharset();
                        }
                        if (StringUtils.isBlank(charset)) {
                            charset = "UTF-8";
                        }
                        String encodedValue = null;
                        if (query.isUrlencoded()) {
                            encodedValue = URLUtils.allEncode(strValue, charset);
                        } else if (allowEncodeBraceInQueryValue) {
                            encodedValue = URLUtils.queryValueEncode(strValue, charset);
                        } else {
                            encodedValue = URLUtils.queryValueWithBraceEncode(strValue, charset);
                        }
                        builder.append(encodedValue);
                    }
                }
                count++;
            }
        }
        return builder.toString();

    }

    public ForestQueryMap clone(final ForestRequest request) {
        final ForestQueryMap newQueryMap = new ForestQueryMap(request);
        for (SimpleQueryParameter query : queries) {
            newQueryMap.addQuery(query);
        }
        return newQueryMap;
    }
}
