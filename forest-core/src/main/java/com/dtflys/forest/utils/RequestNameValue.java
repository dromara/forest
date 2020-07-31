package com.dtflys.forest.utils;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2016-05-24
 */
public class RequestNameValue {

    private String name;

    private Object value;

    private final boolean inQuery;


    public RequestNameValue(String name, boolean inQuery) {
        this.name = name;
        this.inQuery = inQuery;
    }

    public RequestNameValue(String name, Object value, boolean inQuery) {
        this.name = name;
        this.value = value;
        this.inQuery = inQuery;
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
        return inQuery;
    }
}
