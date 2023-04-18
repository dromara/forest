package org.dromara.forest.http.body;

import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.http.ForestBodyItem;
import org.dromara.forest.utils.ForestDataType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class MultipartBodyItem extends ForestBodyItem {

    private MultipartFile multipartFile;

    public MultipartBodyItem(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

    public void setMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    @Override
    public byte[] getByteArray() {
        try {
            return multipartFile.getBytes();
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
    }

    @Override
    public ForestDataType getDefaultBodyType() {
        return ForestDataType.MULTIPART;
    }

    @Override
    public MultipartBodyItem clone() {
        MultipartBodyItem newBody = new MultipartBodyItem(multipartFile);
        newBody.setDefaultValue(getDefaultValue());
        return newBody;
    }
}
