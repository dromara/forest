package org.dromara.forest.example.springboot3.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.DataFile;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.PostRequest;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.callback.OnProgress;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
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
    Map upload(@DataFile(value = "file") Resource resource);

    @PostRequest(url = "/upload")
    Map upload(@DataFile(value = "file") MultipartFile multipartFile, @Body("fileName") String fileName, OnProgress onProgress);


    @PostRequest(url = "/upload-array")
    Map uploadPathList(@DataFile(value = "files", fileName = "test-img-${_index}.jpg") List<String> pathList);
}
