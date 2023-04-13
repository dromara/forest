package org.dromara.forest.test.http.client;

import org.dromara.forest.annotation.BinaryBody;
import org.dromara.forest.annotation.Body;
import org.dromara.forest.annotation.DataFile;
import org.dromara.forest.annotation.LogEnabled;
import org.dromara.forest.annotation.Post;
import org.dromara.forest.annotation.Var;
import org.dromara.forest.backend.ContentType;

import java.io.File;

public interface BinaryClient {

    @Post(
            url = "http://localhost:${port}/upload-octet-stream/${filename}",
            contentType = ContentType.APPLICATION_OCTET_STREAM
    )
    @LogEnabled(logResponseContent = true)
    String uploadOctetStreamWithByteArray(@Body byte[] body, @Var("filename") String filename);

    @Post(
            url = "http://localhost:${port}/upload-octet-stream/${filename}",
            contentType = ContentType.APPLICATION_OCTET_STREAM
    )
    @LogEnabled(logResponseContent = true)
    String uploadOctetStreamWithFile(@Body File file, @Var("filename") String filename);

    @Post(
            url = "http://localhost:${port}/upload-octet-stream/${filename}",
            contentType = ContentType.APPLICATION_OCTET_STREAM
    )
    @LogEnabled(logResponseContent = true)
    String uploadOctetStreamWithDataFile(@DataFile("file") File file, @Var("filename") String filename);


    @Post(
            url = "http://localhost:${port}/upload-octet-stream/${filename}",
            contentType = ContentType.APPLICATION_OCTET_STREAM
    )
    @LogEnabled(logResponseContent = true)
    String uploadOctetStreamWithBinaryBody(@BinaryBody File file, @Var("filename") String filename);

}
