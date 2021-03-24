package com.dtflys.forest.http;

/**
 * 字符串类型请求体
 * <p>该请求体对象会包装一个字符串, 其字符串会被放置到请求体中</p>
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-09-22 17:30
 */
public class StringRequestBody extends ForestRequestBody {

    private String content;

    public StringRequestBody(String content) {
        super(BodyType.STRING);
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }

    @Override
    public byte[] getByteArray() {
        return content.getBytes();
    }
}
