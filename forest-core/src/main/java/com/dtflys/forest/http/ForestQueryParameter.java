package com.dtflys.forest.http;

/**
 * Forest Query 参数类
 * 
 * @param <SELF> 指向自己的泛型类
 */
public interface ForestQueryParameter<SELF extends ForestQueryParameter> {
    static SimpleQueryParameter createSimpleQueryParameter(ForestQueryMap queries, Object value) {
        return new SimpleQueryParameter(queries, String.valueOf(value), null);
    }

    /**
     * 获取 Query 参数名称
     * 
     * @return Query 参数名称
     */
    String getName();

    /**
     * 获取 Query 参数值
     * 
     * @return Query 参数值
     */
    Object getValue();

    /**
     * 设置 Query 参数值
     * 
     * @param value Query 参数值
     * @return 自己
     */
    SELF setValue(Object value);

    /**
     * 是否做 URLEncode
     * 
     * @return {@code true}: 做URLEncode, {@code false}: 不做
     */
    boolean isUrlencoded();

    /**
     * 设置是否做 URLEncode
     * 
     * @param urlencoded {@code true}: 做URLEncode, {@code false}: 不做
     * @return 自己
     */
    SELF setUrlencoded(boolean urlencoded);

    /**
     * 获取参数字符集
     * 
     * @return 字符集名称
     */
    String getCharset();

    /**
     * 设置参数字符集
     * 
     * @param charset 字符集名称
     * @return 自己
     */
    SELF setCharset(String charset);

    /**
     * 参数是否来自 URL
     * 
     * @return {@code true}: 是, {@code false}: 否
     */
    boolean isFromUrl();

    /**
     * 获取 Query 参数的默认值
     * 
     * @return 默认值
     */
    String getDefaultValue();

    /**
     * 设置 Query 参数的默认值
     * 
     * @param defaultValue 默认值
     * @return 自己
     */
    SELF setDefaultValue(String defaultValue);
}
