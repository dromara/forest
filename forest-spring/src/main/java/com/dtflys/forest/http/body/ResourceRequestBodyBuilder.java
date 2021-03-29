package com.dtflys.forest.http.body;

import org.springframework.core.io.Resource;

public class ResourceRequestBodyBuilder extends RequestBodyBuilder<Resource, ResourceRequestBody, ResourceRequestBodyBuilder> {

    @Override
    public ResourceRequestBody build() {
        if (data == null) {
            return null;
        }
        ResourceRequestBody body = new ResourceRequestBody(data);
        body.setDefaultValue(defaultValue);
        return body;
    }
}
