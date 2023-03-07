package com.dtflys.forest.http.body;


import org.noear.solon.core.handle.UploadedFile;

public class MultipartRequestBodyBuilder extends RequestBodyBuilder<UploadedFile, MultipartRequestBody, MultipartRequestBodyBuilder> {

    @Override
    public MultipartRequestBody build(UploadedFile data, String defaultValue) {
        if (data == null) {
            return null;
        }
        MultipartRequestBody body = new MultipartRequestBody(data);
        body.setDefaultValue(defaultValue);
        return body;
    }
}
