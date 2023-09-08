package org.dromara.forest.http.body;

import org.springframework.web.multipart.MultipartFile;

public class SpringMultipartRequestBodyBuilder extends RequestBodyBuilder<MultipartFile, SpringMultipartRequestBody, SpringMultipartRequestBodyBuilder> {

    @Override
    public SpringMultipartRequestBody build(MultipartFile data, String defaultValue) {
        if (data == null) {
            return null;
        }
        SpringMultipartRequestBody body = new SpringMultipartRequestBody(data);
        body.setDefaultValue(defaultValue);
        return body;
    }
}
