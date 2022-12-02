package com.dtflys.forest.http;

public class SimpleHeader implements ForestHeader<SimpleHeader, String> {

    /**
     * 请求头名称
     */
    private final String name;

    /**
     * 请求头的值
     */
    private String value;

    public SimpleHeader(String name, String value) {
        this.name = name;
        this.value = value;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public SimpleHeader setValue(String value) {
        this.value = value;
        return this;
    }
}
