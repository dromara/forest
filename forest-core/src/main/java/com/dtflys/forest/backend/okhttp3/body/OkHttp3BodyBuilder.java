package com.dtflys.forest.backend.okhttp3.body;

import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.backend.body.AbstractBodyBuilder;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.multipart.ForestMultipart;
import com.dtflys.forest.utils.RequestNameValue;
import com.dtflys.forest.utils.StringUtils;
import okhttp3.*;

import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-27 18:18
 */
public class OkHttp3BodyBuilder extends AbstractBodyBuilder<Request.Builder> {

    @Override
    protected void setStringBody(Request.Builder builder, ForestRequest request, String text, Charset charset, String contentType, boolean mergeCharset) {
        MediaType mediaType = MediaType.parse(contentType);
        Charset cs = StandardCharsets.UTF_8;
        if (charset != null) {
            cs = charset;
        }
        if (contentType != null) {
            if (mediaType == null) {
                throw new ForestRuntimeException("[Forest] '" + contentType + "' is not a valid content type");
            }
            Charset mtcs = mediaType.charset();
            if (mtcs == null) {
                if (charset != null && mergeCharset) {
                    mediaType = MediaType.parse(contentType + "; charset=" + charset.name().toLowerCase());
                }
            }
        }
        byte[] bytes = text.getBytes(cs);
        RequestBody body = RequestBody.create(mediaType, bytes);
        builder.method(request.getType().getName(), body);
    }


    @Override
    protected void setFileBody(Request.Builder builder,
                               ForestRequest request,
                               Charset charset, String contentType,
                               List<RequestNameValue> nameValueList,
                               List<ForestMultipart> multiparts,
                               LifeCycleHandler lifeCycleHandler) {
        String boundary = request.getBoundary();
        MultipartBody.Builder bodyBuilder = null;
        if (StringUtils.isNotEmpty(boundary)) {
            bodyBuilder = new MultipartBody.Builder(boundary);
        } else {
            bodyBuilder = new MultipartBody.Builder();
        }
        ContentType objContentType = new ContentType(contentType);
        MediaType mediaType = MediaType.parse(objContentType.toStringWithoutParameters());
        if ("multipart".equals(mediaType.type())) {
            bodyBuilder.setType(mediaType);
        }
        ForestJsonConverter jsonConverter = request.getConfiguration().getJsonConverter();
        for (int i = 0; i < nameValueList.size(); i++) {
            RequestNameValue nameValue = nameValueList.get(i);
            if (!nameValue.isInBody()) {
                continue;
            }
            String name = nameValue.getName();
            Object value = nameValue.getValue();
            String partContentType = nameValue.getPartContentType();
            if (StringUtils.isEmpty(partContentType)) {
                partContentType = "text/plain";
            }
            MediaType partMediaType = MediaType.parse(partContentType);
            if (partMediaType.charset() == null) {
                partMediaType.charset(charset);
            }
            RequestBody requestBody = RequestBody.create(partMediaType, MappingTemplate.getParameterValue(jsonConverter, value));
            MultipartBody.Part part = MultipartBody.Part.createFormData(name, null, requestBody);
            bodyBuilder.addPart(part);
        }
        for (ForestMultipart multipart : multiparts) {
            RequestBody fileBody = createFileBody(request, multipart, charset, lifeCycleHandler);
            bodyBuilder.addFormDataPart(multipart.getName(), multipart.getOriginalFileName(), fileBody);
        }

        MultipartBody body = bodyBuilder.build();
        builder.method(request.getType().getName(), body);
    }

    private RequestBody createFileBody(ForestRequest request, ForestMultipart multipart, Charset charset, LifeCycleHandler lifeCycleHandler) {
        RequestBody wrappedBody, requestBody;
        String partContentType = multipart.getContentType();
        MediaType fileMediaType = null;
        if (StringUtils.isNotEmpty(partContentType)) {
            fileMediaType = MediaType.parse(partContentType);
        }

        if (fileMediaType == null) {
            String mimeType = URLConnection.guessContentTypeFromName(multipart.getOriginalFileName());
            if (mimeType == null) {
                // guess this is a video uploading
                fileMediaType = MediaType.parse(com.dtflys.forest.backend.ContentType.MULTIPART_FORM_DATA);
            } else {
                fileMediaType = MediaType.parse(mimeType);
            }
        }

        if (fileMediaType.charset() == null) {
            fileMediaType.charset(charset);
        }
        if (multipart.isFile()) {
            requestBody = RequestBody.create(fileMediaType, multipart.getFile());
        } else {
            requestBody = RequestBody.create(fileMediaType, multipart.getBytes());
        }

        wrappedBody = new OkHttpMultipartBody(request, requestBody, lifeCycleHandler);
        return wrappedBody;
    }

    @Override
    protected void setBinaryBody(
            Request.Builder builder,
            ForestRequest request,
            Charset charset,
            String contentType,
            byte[] bytes,
            boolean mergeCharset) {
        if (StringUtils.isBlank(contentType)) {
            contentType = ContentType.APPLICATION_OCTET_STREAM;
        }
        MediaType mediaType = MediaType.parse(contentType);
        Charset mtcs = mediaType.charset();
        if (mtcs == null) {
            if (charset != null && mergeCharset) {
                mediaType = MediaType.parse(contentType + "; charset=" + charset.name().toLowerCase());
            }
        }
        RequestBody body = RequestBody.create(mediaType, bytes);
        builder.method(request.getType().getName(), body);
    }
}
