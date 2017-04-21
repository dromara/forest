package org.forest.utils;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2016-05-24
 */
public class RequestNameValue {

    private String name;

    private Object value;

    public RequestNameValue(String name) {
        this.name = name;
    }

    public RequestNameValue(String name, Object value) {
        this.name = name;
        this.value = value;
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
}
