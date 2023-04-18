package org.dromara.forest.http.body;

import org.springframework.web.multipart.MultipartFile;

public class MultipartRequestBodyBuilder extends RequestBodyBuilder<MultipartFile, MultipartBodyItem, MultipartRequestBodyBuilder> {

    @Override
    public MultipartBodyItem build(MultipartFile data, String defaultValue) {
        if (data == null) {
            return null;
        }
        MultipartBodyItem body = new MultipartBodyItem(data);
        body.setDefaultValue(defaultValue);
        return body;
    }
}
