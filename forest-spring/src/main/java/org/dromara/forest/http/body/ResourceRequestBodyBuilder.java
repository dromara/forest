package org.dromara.forest.http.body;

import org.springframework.core.io.Resource;

public class ResourceRequestBodyBuilder extends RequestBodyBuilder<Resource, ResourceBodyItem, ResourceRequestBodyBuilder> {

    @Override
    public ResourceBodyItem build(Resource data, String defaultValue) {
        if (data == null) {
            return null;
        }
        ResourceBodyItem body = new ResourceBodyItem(data);
        body.setDefaultValue(defaultValue);
        return body;
    }
}
