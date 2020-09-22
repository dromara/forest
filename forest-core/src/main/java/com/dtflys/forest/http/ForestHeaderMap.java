package com.dtflys.forest.http;

import java.util.*;

/**
 * Forest请求头Map
 * <p>该类负责批量管理在Forest请求中所有的请求头信息</p>
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-08-11 12:45
 */
public class ForestHeaderMap {

    private final List<ForestHeader> headers;

    public ForestHeaderMap(List<ForestHeader> headers) {
        this.headers = headers;
    }

    public ForestHeaderMap() {
        this.headers = new LinkedList<>();
    }

    /**
     * 获取本请求头集合的大小
     * @return
     */
    public int size() {
        return headers.size();
    }

    /**
     * 根据请求头名称获取请求体的值
     * @param name 请求头名称
     * @return
     */
    public String getValue(String name) {
        ForestHeader header = getHeader(name);
        if (header != null) {
            return header.getValue();
        }
        return null;
    }

    /**
     * 根据请求头名称获取请求体的值列表
     * @param name 请求头名称
     * @return
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
     * @return
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
     * @return
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
     * @return
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
     * @return
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
     * 当前设置的请求头名称在本集合中已存在的情况下会覆盖原有同名请求头的值，负责便新增一个请求头
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
     * 获取本请求头集合的迭代器对象
     * @return
     */
    public Iterator<ForestHeader> headerIterator() {
        return headers.iterator();
    }

    /**
     * 根据请求头的名称删除请求头
     * @param name
     */
    public void remove(String name) {
        ForestHeader header = getHeader(name);
        if (header != null) {
            headers.remove(header);
        }
    }

}
