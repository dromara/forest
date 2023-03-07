package com.dtflys.forest.example.client;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.callback.OnProgress;
import org.noear.solon.core.handle.UploadedFile;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

@BaseRequest(baseURL = "localhost:8080")
public interface UploadClient {

    @Request(
            url = "/upload",
            type = "post",
            dataType = "json",
            contentType = "multipart/form-data"
    )
    Map upload(@DataFile("file") String filePath, OnProgress onProgress);


    @Post(url = "/upload")
    Map upload(@DataFile("file") File file, OnProgress onProgress);

    @Post(url = "/upload")
    Map upload(@DataFile(value = "file", fileName = "${1}") byte[] bytes, String filename);

    @Post(url = "/upload")
    Map upload(@DataFile(value = "file", fileName = "${1}") InputStream in, String filename);


    @Post(url = "/upload")
    Map upload(@DataFile(value = "file") URL resource);

    @PostRequest(url = "/upload")
    Map upload(@DataFile(value = "file") UploadedFile multipartFile, @Body("fileName") String fileName, OnProgress onProgress);


    @PostRequest(url = "/upload-array")
    Map uploadPathList(@DataFile(value = "files", fileName = "test-img-${_index}.jpg") List<String> pathList);
}
