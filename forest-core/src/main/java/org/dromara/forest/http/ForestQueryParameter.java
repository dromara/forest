package org.dromara.forest.http;

public interface ForestQueryParameter<SELF extends ForestQueryParameter> {
    static SimpleQueryParameter createSimpleQueryParameter(ForestQueryMap queries, Object value) {
        return new SimpleQueryParameter(queries, String.valueOf(value), null);
    }

    String getName();

    Object getValue();

    SELF setValue(Object value);

    boolean isUrlencoded();

    SELF setUrlencoded(boolean urlencoded);

    String getCharset();

    SELF setCharset(String charset);

    boolean isFromUrl();

    String getDefaultValue();

    SELF setDefaultValue(String defaultValue);
}
