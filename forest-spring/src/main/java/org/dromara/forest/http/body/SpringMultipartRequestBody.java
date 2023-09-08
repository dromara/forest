package org.dromara.forest.http.body;

import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.http.ForestRequestBody;
import org.dromara.forest.utils.ForestDataType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class SpringMultipartRequestBody extends ForestRequestBody {

    private MultipartFile multipartFile;

    public SpringMultipartRequestBody(MultipartFile multipartFile) {
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
    public SpringMultipartRequestBody clone() {
        SpringMultipartRequestBody newBody = new SpringMultipartRequestBody(multipartFile);
        newBody.setDefaultValue(getDefaultValue());
        return newBody;
    }
}
