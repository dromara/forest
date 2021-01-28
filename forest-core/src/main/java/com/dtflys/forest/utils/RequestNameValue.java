package com.dtflys.forest.utils;

import com.dtflys.forest.converter.json.ForestJsonConverter;

import static com.dtflys.forest.mapping.MappingParameter.*;

/**
 * 请求报文中键值对数据的封装
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2016-05-24
 */
public class RequestNameValue {

    private String name;

    private Object value;

    private final int target;

    private String defaultValue;

    /**
     * 子项Content-Type
     */
    private String partContentType;

    /**
     * 请求对象中键值对数据的封装
     *
     * @param name 键值字符串
     * @param target 在请求报文中的目标位置
     */
    public RequestNameValue(String name, int target) {
        this.name = name;
        this.target = target;
    }

    public RequestNameValue(String name, int target, String partContentType) {
        this.name = name;
        this.target = target;
        this.partContentType = partContentType;
    }


    public RequestNameValue(String name, Object value, int target) {
        this.name = name;
        this.value = value;
        this.target = target;
    }

    public RequestNameValue(String name, Object value, int target, String partContentType) {
        this.name = name;
        this.value = value;
        this.target = target;
        this.partContentType = partContentType;
    }



    public String getName() {
        return name;
    }

    public RequestNameValue setName(String name) {
        this.name = name;
        return this;
    }

    public Object getValue() {
        return value;
    }

    public RequestNameValue setValue(Object value) {
        this.value = value;
        return this;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public RequestNameValue setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }


    public boolean isInQuery() {
        return target == TARGET_QUERY;
    }

    public boolean isInBody() {
        return target == TARGET_BODY;
    }

    public boolean isInHeader() {
        return target == TARGET_HEADER;
    }

    public int getTarget() {
        return target;
    }

    public String getPartContentType() {
        return partContentType;
    }

    public RequestNameValue setPartContentType(String partContentType) {
        this.partContentType = partContentType;
        return this;
    }
}
