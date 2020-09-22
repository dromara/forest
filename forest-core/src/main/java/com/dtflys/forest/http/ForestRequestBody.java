package com.dtflys.forest.http;

public class ForestRequestBody {

    public enum BodyType {
        STRING,
        NAME_VALUE,
        OBJECT,
        MULTIPART
    }

    protected final BodyType type;

    public ForestRequestBody(BodyType type) {
        this.type = type;
    }


    public BodyType getType() {
        return type;
    }

}
