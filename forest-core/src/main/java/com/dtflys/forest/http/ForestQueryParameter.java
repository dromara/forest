package com.dtflys.forest.http;

/**
 * Forest请求URL的Query参数项
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-08-11 12:45
 */
public class ForestQueryParameter {

    /**
     * 参数名
     */
    private final String name;

    /**
     * 参数值
     */
    private Object value;

    public static ForestQueryParameter createSimpleQueryParameter(Object value) {
        return new ForestQueryParameter(String.valueOf(value), null);
    }

    public ForestQueryParameter(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public ForestQueryParameter(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
