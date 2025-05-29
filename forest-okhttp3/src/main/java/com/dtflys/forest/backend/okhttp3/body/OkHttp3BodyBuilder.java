package com.dtflys.forest.backend.okhttp3.body;

import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.backend.body.AbstractBodyBuilder;
import com.dtflys.forest.backend.okhttp3.body.OkHttpMultipartBody;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.http.body.NameValueRequestBody;
import com.dtflys.forest.http.body.ObjectRequestBody;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.multipart.ForestMultipart;
import com.dtflys.forest.utils.StringUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

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
        final Charset cs = charset != null ? Charset.forName(charset) : StandardCharsets.UTF_8;
        if (contentType != null) {
            if (mediaType == null) {
                throw new ForestRuntimeException("[Forest] '" + contentType + "' is not a valid content type");
            }
            final Charset mtcs = mediaType.charset();
            if (mtcs == null) {
                if (charset != null && mergeCharset) {
                    mediaType = MediaType.parse(contentType + ";charset=" + charset);
                }
            }
        }
        final byte[] bytes = text.getBytes(cs);
        final RequestBody body = RequestBody.create(mediaType, bytes);
        builder.method(request.getType().getName(), body);
    }

    private void addMultipart(MultipartBody.Builder bodyBuilder,
                              String name, Object value, String contentType,
                              Charset charset, ForestJsonConverter jsonConverter) {
        if (value == null) {
            return;
        }
        if (StringUtils.isEmpty(contentType)) {
            contentType = "text/plain";
        }
        final MediaType partMediaType = MediaType.parse(contentType);
        if (partMediaType.charset() == null) {
            partMediaType.charset(charset);
        }
        final RequestBody requestBody = RequestBody.create(partMediaType, MappingTemplate.getParameterValue(jsonConverter, value));
        final MultipartBody.Part part = MultipartBody.Part.createFormData(name, null, requestBody);
        bodyBuilder.addPart(part);
    }


    @Override
    protected void setFileBody(Request.Builder builder,
                               ForestRequest request,
                               Charset charset, String contentType,
                               LifeCycleHandler lifeCycleHandler) {
        final String boundary = request.getBoundary();
        final MultipartBody.Builder bodyBuilder = StringUtils.isNotEmpty(boundary) ?
                new MultipartBody.Builder(boundary) : new MultipartBody.Builder();
        final ContentType objContentType = new ContentType(contentType, request.mineCharset());
        final MediaType mediaType = MediaType.parse(objContentType.toStringWithoutParameters());
        if ("multipart".equals(mediaType.type())) {
            bodyBuilder.setType(mediaType);
        }
        final ForestJsonConverter jsonConverter = request.getConfiguration().getJsonConverter();
        final List<ForestMultipart> multiparts = request.getMultiparts();
        int partsCount = 0;
        for (ForestRequestBody item : request.body()) {
            if (item instanceof NameValueRequestBody) {
                final NameValueRequestBody nameValueItem = (NameValueRequestBody) item;
                final String name = nameValueItem.getName();
                final Object value = nameValueItem.getValue();
                final String partContentType = nameValueItem.getContentType();
                partsCount++;
                addMultipart(bodyBuilder, name, value, partContentType, charset, jsonConverter);
            } else if (item instanceof ObjectRequestBody) {
                final Object obj = ((ObjectRequestBody) item).getObject();
                if (obj == null) {
                    continue;
                }
                final Map<String, Object> attrs = jsonConverter.convertObjectToMap(obj, request);
                for (Map.Entry<String, Object> entry : attrs.entrySet()) {
                    final String name = entry.getKey();
                    final Object value = entry.getValue();
                    partsCount++;
                    addMultipart(bodyBuilder, name, value, null, charset, jsonConverter);
                }
            }
        }
        for (ForestMultipart multipart : multiparts) {
            partsCount++;
            final RequestBody fileBody = createFileBody(request, multipart, charset, lifeCycleHandler);
            bodyBuilder.addFormDataPart(multipart.getName(), multipart.getOriginalFileName(), fileBody);
        }
        // 没有任何 parts 的时候
        // 绕过 okhttp 的空 multipart 错误
        if (partsCount == 0) {
            addMultipart(bodyBuilder, "", "", "text/pain", charset, jsonConverter);
        }
        final MultipartBody body = bodyBuilder.build();
        builder.method(request.getType().getName(), body);
    }

    private RequestBody createFileBody(ForestRequest request, ForestMultipart multipart, Charset charset, LifeCycleHandler lifeCycleHandler) {
        RequestBody wrappedBody, requestBody;
        final String partContentType = multipart.getContentType();
        MediaType fileMediaType = null;
        if (StringUtils.isNotEmpty(partContentType)) {
            fileMediaType = MediaType.parse(partContentType);
        }

        if (fileMediaType == null) {
            final String mimeType = URLConnection.guessContentTypeFromName(multipart.getOriginalFileName());
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
            final Request.Builder builder,
            final ForestRequest request,
            final String charset,
            final String contentType,
            final byte[] bytes,
            boolean mergeCharset) {
        final String ctype = StringUtils.isBlank(contentType) ? ContentType.APPLICATION_OCTET_STREAM : contentType;
        MediaType mediaType = MediaType.parse(ctype);
        final Charset mtcs = mediaType.charset();
        if (mtcs == null) {
            if (charset != null && mergeCharset) {
                mediaType = MediaType.parse(ctype + ";charset=" + charset);
            }
        }
        final RequestBody body = RequestBody.create(mediaType, bytes);
        builder.method(request.getType().getName(), body);
    }
}
