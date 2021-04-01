package com.dtflys.forest.http.body;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequestBody;
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
}
