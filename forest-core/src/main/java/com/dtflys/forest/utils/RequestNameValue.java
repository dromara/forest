package com.dtflys.forest.utils;

import static com.dtflys.forest.mapping.MappingParameter.*;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2016-05-24
 */
public class RequestNameValue {

    private String name;

    private Object value;

    private final int target;


    public RequestNameValue(String name, int target) {
        this.name = name;
        this.target = target;
    }

    public RequestNameValue(String name, Object value, int target) {
        this.name = name;
        this.value = value;
        this.target = target;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
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
}
