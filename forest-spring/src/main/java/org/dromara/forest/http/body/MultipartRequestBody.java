package org.dromara.forest.http.body;

import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.http.ForestRequestBody;
import org.dromara.forest.utils.ForestDataType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class MultipartRequestBody extends ForestRequestBody {

    private MultipartFile multipartFile;

    public MultipartRequestBody(MultipartFile multipartFile) {
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
    public MultipartRequestBody clone() {
        MultipartRequestBody newBody = new MultipartRequestBody(multipartFile);
        newBody.setDefaultValue(getDefaultValue());
        return newBody;
    }
}
