package org.dromara.forest.http.body;

import org.springframework.core.io.Resource;

public class SpringResourceRequestBodyBuilder extends RequestBodyBuilder<Resource, SpringResourceRequestBody, SpringResourceRequestBodyBuilder> {

    @Override
    public SpringResourceRequestBody build(Resource data, String defaultValue) {
        if (data == null) {
            return null;
        }
        SpringResourceRequestBody body = new SpringResourceRequestBody(data);
        body.setDefaultValue(defaultValue);
        return body;
    }
}
