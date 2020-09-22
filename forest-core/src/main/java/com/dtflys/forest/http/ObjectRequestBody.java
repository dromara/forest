package com.dtflys.forest.http;

public class ObjectRequestBody extends ForestRequestBody {

    private Object object;

    public ObjectRequestBody(Object object) {
        super(ForestRequestBody.BodyType.OBJECT);
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return String.valueOf(object);
    }
}
