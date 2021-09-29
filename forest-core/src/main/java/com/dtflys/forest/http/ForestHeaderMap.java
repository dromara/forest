package com.dtflys.forest.http;

import java.util.*;

/**
 * Forest请求头Map
 * <p>该类负责批量管理在Forest请求中所有的请求头信息</p>
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-08-11 12:45
 */
public class ForestHeaderMap implements Map<String, String> {

    private final List<ForestHeader> headers;

    public ForestHeaderMap(List<ForestHeader> headers) {
        this.headers = headers;
    }

    public ForestHeaderMap() {
        this.headers = new LinkedList<>();
    }

    /**
     * 获取本请求头集合的大小
     * @return 本请求头集合的大小
     */
    @Override
    public int size() {
        return headers.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            return false;
        }
        String name = key.toString();
        for (ForestHeader header : headers) {
            if (header.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        for (ForestHeader header : headers) {
            Object headerVal = header.getValue();
            if (headerVal == null) {
                if (value == null) {
                    return true;
                }
                continue;
            }
            if (headerVal.equals(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String get(Object key) {
        if (key == null) {
            return null;
        }
        String name = key.toString();
        ForestHeader header = getHeader(name);
        if (header == null) {
            return null;
        }
        return header.getValue();
    }


    @Override
    public String put(String key, String value) {
        ForestHeader header = getHeader(key);
        if (header != null) {
            header.setValue(value);
        } else {
            ForestHeader newHeader = new ForestHeader(key, value);
            addHeader(newHeader);
        }
        return value;
    }

    /**
     * 根据请求头的名称删除请求头
     *
     * @param key 请求头名称
     * @return 被删除的请求头的值
     */
    @Override
    public String remove(Object key) {
        if (key == null) {
            return null;
        }
        String name = key.toString();
        for (int i = headers.size() - 1; i >= 0; i--) {
            ForestHeader header = headers.get(i);
            if (header.getName().equalsIgnoreCase(name)) {
                ForestHeader removedHeader = headers.remove(i);
                return removedHeader.getValue();
            }
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        if (m == null) {
            return;
        }
        for (Entry<? extends String, ? extends String> entry : m.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();
            put(name, value);
        }
    }

    @Override
    public void clear() {
        headers.clear();
    }

    @Override
    public Set<String> keySet() {
        Set<String> set = new HashSet<>();
        for (ForestHeader header : headers) {
            set.add(header.getName());
        }
        return set;
    }

    @Override
    public Collection<String> values() {
        List<String> list = new ArrayList<>();
        for (ForestHeader header : headers) {
            String val = header.getValue();
            if (val != null) {
                list.add(val);
            }
        }
        return list;
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        Set<Entry<String, String>> set = new HashSet<>();
        for (ForestHeader header : headers) {
            Entry<String, String> entry = new Entry<String, String>() {

                @Override
                public String getKey() {
                    return header.getName();
                }

                @Override
                public String getValue() {
                    return header.getValue();
                }

                @Override
                public String setValue(String value) {
                    String oldValue = header.getValue();
                    header.setValue(value);
                    return oldValue;
                }
            };
            set.add(entry);
        }
        return set;
    }

    /**
     * 根据请求头名称获取请求头的值
     * @param name 请求头名称
     * @return 请求头的值
     */
    public String getValue(String name) {
        ForestHeader header = getHeader(name);
        if (header != null) {
            return header.getValue();
        }
        return null;
    }

    /**
     * 根据请求头名称获取请求头的值列表
     * @param name 请求头名称
     * @return 请求头的值列表
     */
    public List<String> getValues(String name) {
        List<String> results = new ArrayList<>(2);
        for (ForestHeader header : headers) {
            if (header.getName().equalsIgnoreCase(name)) {
                results.add(header.getValue());
            }
        }
        return Collections.unmodifiableList(results);
    }

    /**
     * 根据请求头名称获取请求头对象
     * @param name 请求头名称
     * @return 请求头对象，{@link ForestHeader}类实例
     */
    public ForestHeader getHeader(String name) {
        for (ForestHeader header : headers) {
            if (header.getName().equalsIgnoreCase(name)) {
                return header;
            }
        }
        return null;
    }

    /**
     * 根据请求头名称获取请求头对象列表
     * @param name 请求头名称
     * @return 请求头对象列表，列表项为{@link ForestHeader}类实例
     */
    public List<ForestHeader> getHeaders(String name) {
        List<ForestHeader> results = new ArrayList<>(2);
        for (ForestHeader header : headers) {
            if (header.getName().equalsIgnoreCase(name)) {
                results.add(header);
            }
        }
        return results;
    }

    /**
     * 获取所有请求头的名称列表
     * @return 所有请求头的名称列表
     */
    public List<String> names() {
        List<String> results = new ArrayList<>(headers.size());
        for (ForestHeader header : headers) {
            results.add(header.getName());
        }
        return Collections.unmodifiableList(results);
    }

    /**
     * 获取所有请求头的值
     * @return 所有请求头的值列表
     */
    public List<String> getValues() {
        List<String> results = new ArrayList<>(headers.size());
        for (ForestHeader header : headers) {
            results.add(header.getValue());
        }
        return Collections.unmodifiableList(results);
    }

    /**
     * 添加请求头
     * @param header 请求头对象
     */
    public void addHeader(ForestHeader header) {
        headers.add(header);
    }

    /**
     * 添加请求头
     * @param name 请求头名称
     * @param value 请求头的值
     */
    public void addHeader(String name, String value) {
        addHeader(new ForestHeader(name, value));
    }

    /**
     * 设置请求头
     * 当前设置的请求头名称在本集合中已存在的情况下会覆盖原有同名请求头的值，否则便新增一个请求头
     * @param name 请求头名称
     * @param value 请求头的值
     */
    public void setHeader(String name, String value) {
        ForestHeader header = getHeader(name);
        if (header != null) {
            header.setValue(value);
        } else {
            addHeader(name, value);
        }
    }

    /**
     * 通过 Map 批量设置请求头
     *
     * @param map {@link Map}对象
     */
    public void setHeader(Map map) {
        if (map == null) {
            return;
        }
        for (Object key : map.keySet()) {
            Object value = map.get(key);
            setHeader(String.valueOf(key), String.valueOf(value));
        }
    }


    /**
     * 获取本请求头集合的迭代器对象
     *
     * @return 本请求头集合的迭代器对象
     */
    public Iterator<ForestHeader> headerIterator() {
        return headers.iterator();
    }

    /**
     * 克隆Forest请求头Map
     *
     * @return 新的Forest请求头Map
     */
    public ForestHeaderMap clone() {
        ForestHeaderMap newHeaderMap = new ForestHeaderMap();
        for (ForestHeader header : headers) {
            newHeaderMap.addHeader(header);
        }
        return newHeaderMap;
    }
}
