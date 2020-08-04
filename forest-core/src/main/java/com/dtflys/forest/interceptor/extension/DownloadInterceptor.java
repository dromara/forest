package com.dtflys.forest.interceptor.extension;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.binary.DefaultBinaryConverter;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.utils.ForestDataType;

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
        ForestConverter converter = request.getConfiguration().getConverter(ForestDataType.BINARY);
        InputStream in = (InputStream) converter.convertToJavaObject(data, InputStream.class);
        byte[] buffer = new byte[4096];
        int len;
        String path = dir.getPath() + File.separator + filename;
        try (FileOutputStream out = new FileOutputStream(path)) {
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            addAttribute(request, "filePath", path);
        } catch (FileNotFoundException e) {
            throw new ForestRuntimeException(e);
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                throw new ForestRuntimeException(e);
            }
        }

    }
}
