package com.dtflys.forest.http;

/**
 * Forest请求头
 * <p>该类封装单个Forest请求头信息</p>
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-08-11 12:45
 */
public class ForestHeader {

    /**
     * 请求头名称
     */
    private final String name;

    /**
     * 请求头的值
     */
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
