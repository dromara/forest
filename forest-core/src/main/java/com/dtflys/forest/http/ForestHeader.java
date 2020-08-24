package com.dtflys.forest.http;

/**
 * Forest包装的请求头
 */
public class ForestHeader {

    private final String name;

    private String value;

    public ForestHeader(String name, String value) {
        this.name = name;
        this.value = value;
    }


    public String getName() {
        return name;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
