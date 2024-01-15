package com.dtflys.forest.http;

public abstract class AbstractQueryParameter<SELF extends AbstractQueryParameter<SELF>> implements ForestQueryParameter<SELF> {

    protected final SELF self = (SELF) this;

    ForestQueryMap queries;

    /**
     * 是否做URLEncode
     */
    protected boolean urlencoded = false;

    /**
     * 字符集
     */
    protected String charset;

    /**
     * 默认值
     */
    protected String defaultValue;

    /**
     * 是否源自URL
     * <p>源自URL的Query参数在每次调用{@link ForestRequest#setUrlTemplate(String)}时都会被清理掉</p>
     */
    private final boolean fromUrl;

    protected AbstractQueryParameter(ForestQueryMap queries, boolean fromUrl) {
        this.queries = queries;
        this.fromUrl = fromUrl;
    }


    @Override
    public boolean isUrlencoded() {
        return urlencoded;
    }

    @Override
    public SELF setUrlencoded(boolean urlencoded) {
        this.urlencoded = urlencoded;
        return self;
    }

    @Override
    public String getCharset() {
        return charset;
    }

    @Override
    public SELF setCharset(String charset) {
        this.charset = charset;
        return self;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public SELF setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return self;
    }

    /**
     * 是否源自URL
     * <p>源自URL的Query参数在每次调用{@link ForestRequest#setUrlTemplate(String)}时都会被清理掉</p>
     *
     * @return {@code true}: 源自URL, {@code false}: 否
     * @since 1.5.0-BETA5
     */
    @Override
    public boolean isFromUrl() {
        return fromUrl;
    }
}
