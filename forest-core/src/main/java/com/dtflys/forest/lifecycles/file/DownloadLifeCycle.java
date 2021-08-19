package com.dtflys.forest.lifecycles.file;

import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.extensions.DownloadFile;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.ReflectUtils;
import com.dtflys.forest.utils.StringUtils;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.lang.reflect.Type;

/**
 * 文件下载生命周期
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-04 02:29
 */
public class DownloadLifeCycle implements MethodAnnotationLifeCycle<DownloadFile, Object> {

    @Override
    public void onMethodInitialized(ForestMethod method, DownloadFile annotation) {
    }


    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        Type resultType = method.getResultType();
        addAttribute(request, "resultType", resultType);
        request.setDownloadFile(true);
    }

    @Override
    public void onSuccess(Object data, ForestRequest request, ForestResponse response) {
        String dirPath = getAttributeAsString(request, "dir");
        String filename = getAttributeAsString(request, "filename");
        Type resultType = getAttribute(request, "resultType", Type.class);

        if (StringUtils.isBlank(filename)) {
            filename = response.getFilename();
        }

        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        InputStream in = null;
        if (data != null && data instanceof byte[]) {
            in = new ByteArrayInputStream((byte[]) data);
        } else {
            try {
                in = response.getInputStream();
            } catch (Exception e) {
                throw new ForestRuntimeException(e);
            }
        }
        String path = dir.getPath() + File.separator + filename;
        File file = new File(path);
        try {
            FileUtils.copyInputStreamToFile(in, file);
            request.addAttachment("file", file);
            if (resultType != null) {
                ForestConverter converter = request.getConfiguration().getConverterMap().get(ForestDataType.AUTO);
                data = converter.convertToJavaObject(file, resultType);
                response.setResult(data);
            }
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }

    }
}
