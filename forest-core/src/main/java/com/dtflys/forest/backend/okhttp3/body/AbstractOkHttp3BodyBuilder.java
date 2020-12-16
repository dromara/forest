package com.dtflys.forest.backend.okhttp3.body;

import com.dtflys.forest.backend.body.AbstractBodyBuilder;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.multipart.ForestMultipart;
import com.dtflys.forest.utils.RequestNameValue;
import com.dtflys.forest.utils.StringUtils;
import com.twitter.util.Throw;
import okhttp3.*;
import org.apache.http.entity.ContentType;

import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-27 18:18
 */
public abstract class AbstractOkHttp3BodyBuilder extends AbstractBodyBuilder<Request.Builder> {

    private static Charset DEFAULT_CHARSET = Charset.forName("UTF-8");


    protected abstract void setBody(Request.Builder builder, RequestBody body);

    @Override
    protected void setStringBody(Request.Builder builder, String text, String charset, String contentType, boolean mergeCharset) {
        MediaType mediaType = MediaType.parse(contentType);
        Charset cs = DEFAULT_CHARSET;
        if (StringUtils.isNotEmpty(charset)) {
            try {
                cs = Charset.forName(charset);
            } catch (Throwable th) {
                throw new ForestRuntimeException("[Forest] '" + charset + "' is not a valid charset", th);
            }
        }
        if (contentType != null) {
            if (mediaType == null) {
                throw new ForestRuntimeException("[Forest] '" + contentType + "' is not a valid content type");
            }
            Charset mtcs = mediaType.charset();
            if (mtcs == null) {
                if (StringUtils.isNotEmpty(charset) && mergeCharset) {
                    mediaType = MediaType.parse(contentType + "; charset=" + charset.toLowerCase());
                }
            }
        }
        byte[] bytes = text.getBytes(cs);

        RequestBody body = RequestBody.create(mediaType, bytes);
        setBody(builder, body);
    }


    @Override
    protected void setFormBody(Request.Builder builder, ForestRequest request, String charset, String contentType, List<RequestNameValue> nameValueList) {
        ForestJsonConverter jsonConverter = request.getConfiguration().getJsonConverter();
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        nameValueList = processFromNameValueList(nameValueList, request.getConfiguration());
        for (int i = 0; i < nameValueList.size(); i++) {
            RequestNameValue nameValue = nameValueList.get(i);
            if (!nameValue.isInBody()) {
                continue;
            }
            String name = nameValue.getName();
            Object value = nameValue.getValue();
            bodyBuilder.addEncoded(name, MappingTemplate.getFormValueString(jsonConverter, value));
        }

        FormBody body = bodyBuilder.build();
        setBody(builder, body);
    }

    @Override
    protected void setFileBody(Request.Builder builder,
                               ForestRequest request,
                               String charset, String contentType,
                               List<RequestNameValue> nameValueList,
                               List<ForestMultipart> multiparts,
                               LifeCycleHandler lifeCycleHandler) {
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
        MediaType mediaType = MediaType.parse(contentType);
        bodyBuilder.setType(mediaType);
        ForestJsonConverter jsonConverter = request.getConfiguration().getJsonConverter();
        Charset ch = Charset.forName(charset);
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
                partMediaType.charset(ch);
            }
            RequestBody requestBody = RequestBody.create(partMediaType, MappingTemplate.getParameterValue(jsonConverter, value));
            MultipartBody.Part part = MultipartBody.Part.createFormData(name, null, requestBody);
            bodyBuilder.addPart(part);
        }
        for (ForestMultipart multipart : multiparts) {
            RequestBody fileBody = createFileBody(request, multipart, ch, lifeCycleHandler);
            bodyBuilder.addFormDataPart(multipart.getName(), multipart.getOriginalFileName(), fileBody);
        }

        MultipartBody body = bodyBuilder.build();
        setBody(builder, body);
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

}
