package com.dtflys.forest.http;

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
}
