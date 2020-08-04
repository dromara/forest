package com.dtflys.forest.interceptor.extension;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.binary.DefaultBinaryConverter;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.utils.ForestDataType;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DownloadInterceptor implements Interceptor<Object> {


    @Override
    public void afterExecute(ForestRequest request, ForestResponse response) {

    }

    @Override
    public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {

    }

    @Override
    public void onSuccess(Object data, ForestRequest request, ForestResponse response) {
        String dirPath = getAttributeAsString(request, "dir");
        String filename = getAttributeAsString(request, "filename");
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        InputStream in = null;
        try {
            in = response.getInputStream();
        } catch (Exception e) {
            throw new ForestRuntimeException(e);
        }
        String path = dir.getPath() + File.separator + filename;
        File file = new File(path);
        try {
            FileUtils.copyInputStreamToFile(in, file);
            request.addAttachment("file", file);
            if (data != null) {
                ForestConverter converter = request.getConfiguration().getConverterMap().get(ForestDataType.BINARY);
                data = converter.convertToJavaObject(file, data.getClass());
                response.setResult(data);
            }
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }

    }
}
