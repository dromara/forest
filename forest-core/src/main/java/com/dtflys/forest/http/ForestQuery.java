package com.dtflys.forest.http;

public class ForestQuery {

    /**
     * 参数名
     */
    private final String name;

    /**
     * 参数值
     */
    private String value;

    public ForestQuery(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public ForestQuery(String name) {
        this.name = name;
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
