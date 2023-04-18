package org.dromara.forest.backend.okhttp3.body;

import org.dromara.forest.backend.ContentType;
import org.dromara.forest.backend.body.AbstractBodyBuilder;
import org.dromara.forest.converter.json.ForestJsonConverter;
import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.handler.LifeCycleHandler;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestRequestBody;
import org.dromara.forest.http.body.NameValueRequestBody;
import org.dromara.forest.http.body.ObjectRequestBody;
import org.dromara.forest.mapping.MappingTemplate;
import org.dromara.forest.multipart.ForestMultipart;
import org.dromara.forest.utils.StringUtil;
import okhttp3.*;

import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-27 18:18
 */
public class OkHttp3BodyBuilder extends AbstractBodyBuilder<Request.Builder> {

    @Override
    protected void setStringBody(Request.Builder builder, ForestRequest request, String text, String charset, String contentType, boolean mergeCharset) {
        MediaType mediaType = MediaType.parse(contentType);
        Charset cs = StandardCharsets.UTF_8;
        if (charset != null) {
            cs = Charset.forName(charset);
        }
        if (contentType != null) {
            if (mediaType == null) {
                throw new ForestRuntimeException("[Forest] '" + contentType + "' is not a valid content type");
            }
            Charset mtcs = mediaType.charset();
            if (mtcs == null) {
                if (charset != null && mergeCharset) {
                    mediaType = MediaType.parse(contentType + "; charset=" + charset);
                }
            }
        }
        byte[] bytes = text.getBytes(cs);
        RequestBody body = RequestBody.create(mediaType, bytes);
        builder.method(request.getType().getName(), body);
    }

    private void addMultipart(MultipartBody.Builder bodyBuilder,
                              String name, Object value, String contentType,
                              Charset charset, ForestJsonConverter jsonConverter) {
        if (value == null) {
            return;
        }
        if (StringUtil.isEmpty(contentType)) {
            contentType = "text/plain";
        }
        MediaType partMediaType = MediaType.parse(contentType);
        if (partMediaType.charset() == null) {
            partMediaType.charset(charset);
        }
        RequestBody requestBody = RequestBody.create(partMediaType, MappingTemplate.getParameterValue(jsonConverter, value));
        MultipartBody.Part part = MultipartBody.Part.createFormData(name, null, requestBody);
        bodyBuilder.addPart(part);
    }


    @Override
    protected void setFileBody(Request.Builder builder,
                               ForestRequest request,
                               Charset charset, String contentType,
                               LifeCycleHandler lifeCycleHandler) {
        String boundary = request.getBoundary();
        MultipartBody.Builder bodyBuilder = null;
        if (StringUtil.isNotEmpty(boundary)) {
            bodyBuilder = new MultipartBody.Builder(boundary);
        } else {
            bodyBuilder = new MultipartBody.Builder();
        }
        ContentType objContentType = new ContentType(contentType, request.mineCharset());
        MediaType mediaType = MediaType.parse(objContentType.toStringWithoutParameters());
        if ("multipart".equals(mediaType.type())) {
            bodyBuilder.setType(mediaType);
        }
        ForestJsonConverter jsonConverter = request.getConfiguration().getJsonConverter();
        List<ForestMultipart> multiparts = request.getMultiparts();
        int partsCount = 0;
        for (ForestRequestBody item : request.body()) {
            if (item instanceof NameValueRequestBody) {
                NameValueRequestBody nameValueItem = (NameValueRequestBody) item;
                String name = nameValueItem.getName();
                Object value = nameValueItem.getValue();
                String partContentType = nameValueItem.getContentType();
                partsCount++;
                addMultipart(bodyBuilder, name, value, partContentType, charset, jsonConverter);
            } else if (item instanceof ObjectRequestBody) {
                Object obj = ((ObjectRequestBody) item).getObject();
                if (obj == null) {
                    continue;
                }
                Map<String, Object> attrs = jsonConverter.convertObjectToMap(obj, request);
                for (Map.Entry<String, Object> entry : attrs.entrySet()) {
                    String name = entry.getKey();
                    Object value = entry.getValue();
                    partsCount++;
                    addMultipart(bodyBuilder, name, value, null, charset, jsonConverter);
                }
            }
        }
        for (ForestMultipart multipart : multiparts) {
            partsCount++;
            RequestBody fileBody = createFileBody(request, multipart, charset, lifeCycleHandler);
            bodyBuilder.addFormDataPart(multipart.getName(), multipart.getOriginalFileName(), fileBody);
        }
        // 没有任何 parts 的时候
        // 绕过 okhttp 的空 multipart 错误
        if (partsCount == 0) {
            addMultipart(bodyBuilder, "", "", "text/pain", charset, jsonConverter);
        }
        MultipartBody body = bodyBuilder.build();
        builder.method(request.getType().getName(), body);
    }

    private RequestBody createFileBody(ForestRequest request, ForestMultipart multipart, Charset charset, LifeCycleHandler lifeCycleHandler) {
        RequestBody wrappedBody, requestBody;
        String partContentType = multipart.getContentType();
        MediaType fileMediaType = null;
        if (StringUtil.isNotEmpty(partContentType)) {
            fileMediaType = MediaType.parse(partContentType);
        }

        if (fileMediaType == null) {
            String mimeType = URLConnection.guessContentTypeFromName(multipart.getOriginalFileName());
            if (mimeType == null) {
                // guess this is a video uploading
                fileMediaType = MediaType.parse(ContentType.MULTIPART_FORM_DATA);
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
            String charset,
            String contentType,
            byte[] bytes,
            boolean mergeCharset) {
        if (StringUtil.isBlank(contentType)) {
            contentType = ContentType.APPLICATION_OCTET_STREAM;
        }
        MediaType mediaType = MediaType.parse(contentType);
        Charset mtcs = mediaType.charset();
        if (mtcs == null) {
            if (charset != null && mergeCharset) {
                mediaType = MediaType.parse(contentType + "; charset=" + charset);
            }
        }
        RequestBody body = RequestBody.create(mediaType, bytes);
        builder.method(request.getType().getName(), body);
    }
}
