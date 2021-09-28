package com.dtflys.forest.http;

import com.dtflys.forest.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Forest请求Query参数Map
 * <p>该类负责批量管理在Forest请求中所有的请求Query参数</p>
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-08-11 12:45
 */
public class ForestQueryMap implements Map<String, Object> {

    private final List<ForestQueryParameter> queries;

    public ForestQueryMap() {
        this.queries = new LinkedList<>();
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
        String name = key.toString();
        for (ForestQueryParameter query : queries) {
            if (query.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        for (ForestQueryParameter query : queries) {
            Object queryVal = query.getValue();
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

    public ForestQueryParameter getQuery(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        for (ForestQueryParameter query : queries) {
            if (query.getName().equalsIgnoreCase(name)) {
                return query;
            }
        }
        return null;
    }

    public List<ForestQueryParameter> getQueries(String name) {
        List<ForestQueryParameter> list = new LinkedList<>();
        if (StringUtils.isEmpty(name)) {
            return list;
        }
        for (ForestQueryParameter query : queries) {
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
        String name = key.toString();
        ForestQueryParameter query = getQuery(name);
        if (query == null) {
            return null;
        }
        return query.getValue();
    }

    public void addQuery(ForestQueryParameter query) {
        queries.add(query);
    }

    public void addQuery(String name, Object value) {
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
            queries.add(new ForestQueryParameter(name, value, isUrlEncode, charset));
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
        addQuery(name, collection, true, null);
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
            Object value = map.get(key);
            addQuery(String.valueOf(key), value, isUrlEncode, charset);
        }
    }


    /**
     * 添加 JSON Query 参数
     *
     * @param name 参数名
     * @param value 参数值
     * @since 1.5.4
     */
    public void addJSONQuery(String name, Object value) {
        queries.add(new ForestQueryParameter(name, value));
    }



    @Override
    public Object put(String key, Object value) {
        ForestQueryParameter query = getQuery(key);
        if (query != null) {
            query.setValue(value);
        } else {
            ForestQueryParameter newQuery = new ForestQueryParameter(key, value);
            addQuery(newQuery);
        }
        return value;
    }

    @Override
    public Object remove(Object key) {
        if (key == null) {
            return null;
        }
        String name = key.toString();
        for (int i = queries.size() - 1; i >= 0; i--) {
            ForestQueryParameter query = queries.get(i);
            if (query.getName().equalsIgnoreCase(name)) {
                ForestQueryParameter removedQuery = queries.remove(i);
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
    public List<ForestQueryParameter> removeQueries(String name) {
        List<ForestQueryParameter> list = new LinkedList<>();
        if (name == null) {
            return list;
        }
        for (int i = queries.size() - 1; i >= 0; i--) {
            ForestQueryParameter query = queries.get(i);
            if (query.getName().equalsIgnoreCase(name)) {
                ForestQueryParameter removedQuery = queries.remove(i);
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
            String name = entry.getKey();
            Object value = entry.getValue();
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
        int len = queries.size();
        for (int i = len - 1; i >=0; i--) {
            ForestQueryParameter query = queries.get(i);
            if (query.isFromUrl()) {
                queries.remove(i);
            }
        }
    }

    @Override
    public Set<String> keySet() {
        Set<String> set = new HashSet<>();
        for (ForestQueryParameter query : queries) {
            set.add(query.getName());
        }
        return set;
    }

    @Override
    public Collection<Object> values() {
        List<Object> list = new ArrayList<>();
        for (ForestQueryParameter query : queries) {
            Object val = query.getValue();
            if (val != null) {
                list.add(val);
            }
        }
        return list;
    }

    public List<ForestQueryParameter> queryValues() {
        return queries;
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        Set<Entry<String, Object>> set = new HashSet<>();
        for (ForestQueryParameter query : queries) {
            Entry<String, Object> entry = new Entry<String, Object>() {

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
                    Object oldValue = query.getValue();
                    query.setValue(value);
                    return oldValue;
                }
            };
            set.add(entry);
        }
        return set;
    }

    public ForestQueryMap clone() {
        ForestQueryMap newQueryMap = new ForestQueryMap();
        for (ForestQueryParameter query : queries) {
            newQueryMap.addQuery(query);
        }
        return newQueryMap;
    }
}
