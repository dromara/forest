package com.dtflys.forest.http;

/**
 * 对象类型请求体
 * <p>该请求体对象会包装一个Java对象, 其对象最终会在请求发送前被序列化</p>
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-09-22 17:30
 */
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
