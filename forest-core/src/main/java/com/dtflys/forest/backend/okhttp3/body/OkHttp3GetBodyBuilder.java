package com.dtflys.forest.backend.okhttp3.body;

import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.File;
import java.lang.reflect.Field;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-27 18:52
 */
public class OkHttp3GetBodyBuilder extends AbstractOkHttp3BodyBuilder {

    private static Field field;

    static {
        try {
            field = Request.Builder.class.getDeclaredField("body");
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
        }
    }

    @Override
    protected void setBody(Request.Builder builder, RequestBody body) {
        Request.Builder reqBuilder = builder.get();
        if (body != null && field != null) {
            try {
                field.set(reqBuilder, body);
            } catch (IllegalAccessException e) {
            }
        }
    }
}
