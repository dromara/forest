package org.dromara.forest.http.body;

import org.springframework.core.io.Resource;

public class ResourceRequestBodyBuilder extends RequestBodyBuilder<Resource, ResourceRequestBody, ResourceRequestBodyBuilder> {

    @Override
    public ResourceRequestBody build(Resource data, String defaultValue) {
        if (data == null) {
            return null;
        }
        ResourceRequestBody body = new ResourceRequestBody(data);
        body.setDefaultValue(defaultValue);
        return body;
    }
}
