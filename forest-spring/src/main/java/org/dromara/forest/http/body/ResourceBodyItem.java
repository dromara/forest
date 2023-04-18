package org.dromara.forest.http.body;

import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.http.ForestBodyItem;
import org.dromara.forest.utils.ForestDataType;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

public class ResourceBodyItem extends ForestBodyItem {

    private Resource resource;

    public ResourceBodyItem(Resource resource) {
        this.resource = resource;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public byte[] getByteArray() {
        try {
            InputStream inputStream = resource.getInputStream();
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
    }

    @Override
    public ForestDataType getDefaultBodyType() {
        return ForestDataType.BINARY;
    }

    @Override
    public ResourceBodyItem clone() {
        ResourceBodyItem newBody = new ResourceBodyItem(resource);
        newBody.setDefaultValue(getDefaultValue());
        return newBody;
    }
}
