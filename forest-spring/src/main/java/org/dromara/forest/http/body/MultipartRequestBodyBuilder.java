package org.dromara.forest.http.body;

import org.springframework.web.multipart.MultipartFile;

public class MultipartRequestBodyBuilder extends RequestBodyBuilder<MultipartFile, MultipartRequestBody, MultipartRequestBodyBuilder> {

    @Override
    public MultipartRequestBody build(MultipartFile data, String defaultValue) {
        if (data == null) {
            return null;
        }
        MultipartRequestBody body = new MultipartRequestBody(data);
        body.setDefaultValue(defaultValue);
        return body;
    }
}
