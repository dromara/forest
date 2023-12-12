package com.dtflys.forest.lifecycles.file;

import com.dtflys.forest.ForestGenericClient;
import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.extensions.DownloadFile;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.ForestJoinpoint;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.logging.ForestLogHandler;
import com.dtflys.forest.logging.LogConfiguration;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.ForestProgress;
import com.dtflys.forest.utils.ReflectUtils;
import com.dtflys.forest.utils.StringUtils;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 文件下载生命周期
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-04 02:29
 */
public class DownloadLifeCycle implements MethodAnnotationLifeCycle<DownloadFile, Object> {

    public final static String ATTACHMENT_NAME_FILE = "__file";


    @Override
    public void onMethodInitialized(ForestMethod method, DownloadFile annotation) {
    }


    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        final Type resultType = method.getResultType();
        addAttribute(request, "__resultType", resultType);
        request.setDownloadFile(true);
    }


    @Override
    public ForestJoinpoint beforeExecute(ForestRequest request) {
        if (request.getMethod().getMethod().getDeclaringClass() == ForestGenericClient.class) {
            final Type resultType = getResultType(request.getLifeCycleHandler().getResultType());
            addAttribute(request, "__resultType", resultType);
            request.setDownloadFile(true);
        }
        return proceed();
    }

    private Type getResultType(Type type) {
        if (type == null) {
            return Void.class;
        }
        final Class<?> clazz = ReflectUtils.toClass(type);
        if (ForestResponse.class.isAssignableFrom(clazz)) {
            if (type instanceof ParameterizedType) {
                final Type[] types = ((ParameterizedType) type).getActualTypeArguments();
                if (types.length > 0) {
                    return types[0];
                }
            }
        }
        return type;
    }

    @Override
    public void onProgress(ForestProgress progress) {
    }

    @Override
    public void onSuccess(Object data, ForestRequest request, ForestResponse response) {
        final String dirPath = getAttributeAsString(request, "dir");
        String filename = getAttributeAsString(request, "filename");
        final Type resultType = getAttribute(request, "__resultType", Type.class);

        if (StringUtils.isBlank(filename)) {
            filename = response.getFilename();
        }
        final LogConfiguration logConfiguration = request.getLogConfiguration();
        final ForestLogHandler logHandler = logConfiguration.getLogHandler();
        final File dir = new File(dirPath);
        if (!dir.exists()) {
            try {
                dir.mkdirs();
                if (logConfiguration.isLogEnabled()) {
                    logHandler.logContent("Created directory '" + dirPath + "' successful.");
                }
            } catch (Throwable th) {
                throw new ForestRuntimeException(th);
            }
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
        final String path = dir.getAbsolutePath() + File.separator + filename;
        final File file = new File(path);
        try {
            FileUtils.copyInputStreamToFile(in, file);
            FileUtils.waitFor(file, 10);
            if (logConfiguration.isLogEnabled() || !file.exists()) {
                logHandler.logContent("Saved file '" + path + "' successful.");
            }
            request.addAttachment(ATTACHMENT_NAME_FILE, file);
            if (resultType != null) {
                final ForestConverter converter = request
                        .getConfiguration()
                        .getConverterMap()
                        .get(ForestDataType.AUTO);
                data = converter.convertToJavaObject(file, resultType);
                response.setResult(data);
            }
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
