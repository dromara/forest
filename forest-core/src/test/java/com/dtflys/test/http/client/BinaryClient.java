package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.DataFile;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.backend.ContentType;

public interface BinaryClient {

    @Post(
            url = "http://localhost:8080/upload-octet-stream/${filename}",
            contentType = ContentType.APPLICATION_OCTET_STREAM
    )
    String uploadOctetStreamWithByteArray(@DataFile(value = "file", fileName = "xxx.jpg") byte[] body, @Var("filename") String filename);


}
