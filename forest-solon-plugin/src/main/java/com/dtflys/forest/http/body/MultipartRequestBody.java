package com.dtflys.forest.http.body;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.utils.ForestDataType;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.UploadedFile;

import java.io.IOException;

public class MultipartRequestBody extends ForestRequestBody {

    private UploadedFile multipartFile;

    public MultipartRequestBody(UploadedFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    public UploadedFile getMultipartFile() {
        return multipartFile;
    }

    public void setMultipartFile(UploadedFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    @Override
    public byte[] getByteArray() {
        try {
            return Utils.transferToBytes(multipartFile.getContent());
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
