package com.dtflys.forest.backend.httpclient.body;

import com.dtflys.forest.backend.body.AbstractBodyBuilder;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.http.body.NameValueRequestBody;
import com.dtflys.forest.http.body.ObjectRequestBody;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.multipart.ForestMultipart;
import com.dtflys.forest.utils.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.AbstractContentBody;

import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * HttpClient后端的请求Body构造器
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 14:52
 */
public class HttpclientBodyBuilder<T extends HttpEntityEnclosingRequestBase> extends AbstractBodyBuilder<T> {


    @Override
    protected void setStringBody(T httpReq, ForestRequest request, String text, String charset, String contentType, boolean mergeCharset) {
        StringEntity entity = new StringEntity(text, charset);
        if (charset == null && mergeCharset) {
            if (!contentType.contains("charset=")) {
                contentType = contentType + "; charset=" + charset;
            } else {
                String[] strs = contentType.split("charset=");
                contentType = strs[0] + " charset=" + charset;
            }
            entity.setContentEncoding(charset);
        }
        entity.setContentType(contentType);
        httpReq.setEntity(entity);
    }

    private void addMultipart(MultipartEntityBuilder entityBuilder,
                              String name, Object value, String contentType,
                              Charset charset, ForestJsonConverter jsonConverter) {
        if (value == null) {
            return;
        }
        if (StringUtils.isEmpty(contentType)) {
            contentType = "text/plain";
        }
        String text =  MappingTemplate.getParameterValue(jsonConverter, value);
        ContentType itemContentType = ContentType.create(contentType, charset);
        entityBuilder.addTextBody(name, text, itemContentType);
    }


    @Override
    protected void setFileBody(T httpReq,
                               ForestRequest request,
                               Charset charset,
                               String contentType,
                               LifeCycleHandler lifeCycleHandler) {
        String boundary = request.getBoundary();

        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        if (StringUtils.isNotEmpty(boundary)) {
            entityBuilder.setBoundary(boundary);
        }
        // 解决文件名乱码问题
        ForestJsonConverter jsonConverter = request.config().getJsonConverter();
        Charset httpCharset = charset;
        Charset itemCharset = StandardCharsets.UTF_8;
        if (charset != null) {
            itemCharset = charset;
        }
        boolean needSetMode = false;
        for (ForestRequestBody item : request.body()) {
            if (item instanceof NameValueRequestBody) {
                needSetMode = true;
                NameValueRequestBody nameValueItem = (NameValueRequestBody) item;
                String name = nameValueItem.getName();
                Object value = nameValueItem.getValue();
                String partContentType = nameValueItem.getContentType();
                addMultipart(entityBuilder, name, value, partContentType, itemCharset, jsonConverter);

            } else if (item instanceof ObjectRequestBody) {
                Object obj = ((ObjectRequestBody) item).getObject();
                if (obj == null) {
                    continue;
                }
                needSetMode = true;
                Map<String, Object> attrs = jsonConverter.convertObjectToMap(obj, request);
                for (Map.Entry<String, Object> entry : attrs.entrySet()) {
                    String name = entry.getKey();
                    Object value = entry.getValue();
                    addMultipart(entityBuilder, name, value, null, itemCharset, jsonConverter);
                }
            }
        }
        if (needSetMode) {
            entityBuilder.setCharset(httpCharset);
            entityBuilder.setMode(HttpMultipartMode.RFC6532);
        }
        List<ForestMultipart> multiparts = request.getMultiparts();
        for (ForestMultipart multipart : multiparts) {
            String name = multipart.getName();
            String fileName = multipart.getOriginalFileName();
            String partContentType = multipart.getContentType();

            ContentType ctype = null;

            if (StringUtils.isNotEmpty(partContentType)) {
                ctype = ContentType.create(partContentType, httpCharset);
            }
            if (ctype == null) {
                String mimeType = URLConnection.guessContentTypeFromName(fileName);
                if (mimeType == null) {
                    // guess this is a video uploading
                    ctype = ContentType.create(com.dtflys.forest.backend.ContentType.MULTIPART_FORM_DATA, httpCharset);
                } else {
                    ctype = ContentType.create(mimeType);
                }
            }
            AbstractContentBody contentBody = null;
            if (multipart.isFile()) {
                contentBody = new HttpclientMultipartFileBody(request, multipart.getFile(), ctype, fileName, lifeCycleHandler);
            } else {
                contentBody = new HttpclientMultipartCommonBody(request, multipart, ctype, fileName, lifeCycleHandler);
            }
            entityBuilder.addPart(name, contentBody);
        }
        HttpEntity entity = entityBuilder.build();
        httpReq.setEntity(entity);
    }


    @Override
    protected void setBinaryBody(final T httpReq,
                                 final ForestRequest request,
                                 final String charset,
                                 final String contentType,
                                 final byte[] bytes,
                                 final boolean mergeCharset) {
        final String cType = StringUtils.isBlank(contentType) ? ContentType.APPLICATION_OCTET_STREAM.toString() : (
                charset == null && mergeCharset ? (
                        !contentType.contains("charset=") ? contentType + "; charset=" + charset : contentType.split("charset=")[0] + " charset=" + charset
                ) : contentType
        );
        final ContentType mediaType = ContentType.create(cType, charset);
        final HttpEntity entity = new ByteArrayEntity(bytes, mediaType);
        httpReq.setEntity(entity);
    }


}
