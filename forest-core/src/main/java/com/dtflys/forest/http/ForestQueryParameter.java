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

    /**
     * 是否做URLEncode
     */
    private boolean urlencoded = true;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 是否源自URL
     * <p>源自URL的Query参数在每次调用{@link ForestRequest#setUrl(String)}时都会被清理掉</p>
     */
    private final boolean fromUrl;

    public static ForestQueryParameter createSimpleQueryParameter(Object value) {
        return new ForestQueryParameter(String.valueOf(value), null);
    }

    public ForestQueryParameter(String name, Object value) {
        this(name, value, false);
    }

    public ForestQueryParameter(String name, Object value, boolean fromUrl) {
        this.name = name;
        this.value = value;
        this.fromUrl = fromUrl;
        if (fromUrl) {
            this.urlencoded = false;
        }
    }


    public ForestQueryParameter(String name) {
        this(name, null, false);
    }


    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public ForestQueryParameter setValue(Object value) {
        this.value = value;
        return this;
    }

    public boolean isUrlencoded() {
        return urlencoded;
    }

    public void setUrlencoded(boolean urlencoded) {
        this.urlencoded = urlencoded;
    }

    /**
     * 是否源自URL
     * <p>源自URL的Query参数在每次调用{@link ForestRequest#setUrl(String)}时都会被清理掉</p>
     *
     * @return {@code true}: 源自URL, {@code false}: 否
     * @since 1.5.0-BETA5
     */
    public boolean isFromUrl() {
        return fromUrl;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public ForestQueryParameter setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
}
