package com.dtflys.forest.test.http.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.DataFile;
import com.dtflys.forest.annotation.JSONBody;
import com.dtflys.forest.annotation.LogEnabled;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.PostRequest;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.callback.OnProgress;
import com.dtflys.forest.http.ForestRequest;
import org.bouncycastle.math.raw.Mod;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@BaseRequest(baseURL = "http://localhost:${port}")
@LogEnabled
public interface UploadClient {

    @Request(
            url = "/upload",
            type = "post",
            dataType = "json",
            contentType = "multipart/form-data"
    )
    ForestRequest<Map> upload(@DataFile("file") String filePath, OnProgress onProgress);


    @Post(url = "/upload")
    ForestRequest<Map> upload(@DataFile("file") File file, OnProgress onProgress);

    @Post(url = "/upload")
    ForestRequest<Map> upload_withParams(@DataFile("file") File file, @Body("a") String a, @Body("b") String b);

    @Post(url = "/upload", contentType = ContentType.MULTIPART_FORM_DATA)
    ForestRequest<Map> upload_withParams(@Body("a") String a, @Body("b") String b);


    @Post(url = "/upload")
    ForestRequest<Map> upload(@DataFile(value = "file", fileName = "${1}") byte[] bytes, String filename);

    @Post(url = "/upload", contentType = "multipart/form-data")
    ForestRequest<Map> upload(@DataFile(value = "file", fileName = "${1}") InputStream in, String filename);

    // Path Collection

    @PostRequest(url = "/upload")
    ForestRequest<Map> uploadPathMap(@DataFile(value = "file", fileName = "${_key}") Map<String, String> pathMap);

    @PostRequest(url = "/upload")
    ForestRequest<Map> uploadPathMap2(@DataFile(value = "file_${_index}", fileName = "${_key}") Map<String, String> pathMap);

    @PostRequest(url = "/upload")
    ForestRequest<Map> uploadPathList(@DataFile(value = "file", fileName = "test-img-${_index}.jpg") List<String> pathList);

    @PostRequest(url = "/upload")
    ForestRequest<Map> uploadPathList2(@DataFile(value = "file_${_index}", fileName = "test-img-${_index}.jpg") List<String> pathList);

    @PostRequest(url = "/upload")
    ForestRequest<Map> uploadPathArray(@DataFile(value = "file", fileName = "test-img-${_index}.jpg") String[] pathArray);

    @PostRequest(url = "/upload")
    ForestRequest<Map> uploadPathArray2(@DataFile(value = "file_${_index}", fileName = "test-img-${_index}.jpg") String[] pathArray);


    // Byte Array Collection

    @PostRequest(url = "/upload")
    ForestRequest<Map> uploadByteArrayMap(@DataFile(value = "file", fileName = "${_key}") Map<String, byte[]> byteArrayMap);

    @PostRequest(url = "/upload")
    ForestRequest<Map> uploadByteArrayList(@DataFile(value = "file", fileName = "test-img-${_index}.jpg") List<byte[]> byteArrayList);

    @PostRequest(url = "/upload")
    ForestRequest<Map> uploadByteArrayArray(@DataFile(value = "file", fileName = "test-img-${_index}.jpg") byte[][] byteArrayArray);

    // mixture
    @PostRequest(url = "/upload")
    ForestRequest<Map> imageUploadWithMapParams(String fileName, @DataFile(value = "file", fileName = "${0}") File file,
                                                @Body Map params);

    @PostRequest(url = "/upload")
    ForestRequest<Map> imageUploadWithBodyParams(String fileName, @DataFile(value = "file", fileName = "${0}") File file,
                                                 @Body("a") String a, @Body("b") String b);

    @PostRequest(url = "/upload", contentType = ContentType.MULTIPART_FORM_DATA)
    ForestRequest<Map> imageUploadWithJSONBodyParams(String fileName, @DataFile(value = "file", fileName = "${0}") File file,
                                                 @JSONBody("params") Map params);

    @PostRequest(url = "/upload")
    ForestRequest<Map> imageUploadWithJSONBodyParamsAndWithoutContentType(String fileName, @DataFile(value = "file", fileName = "${0}") File file,
                                                     @JSONBody("params") Map params);


    class Model {
        String id;
        String filename;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }
    }

    @Post(url = "/upload")
    ForestRequest<Map> uploadWithModel(@DataFile("file") File file, @Body Model model);

}
