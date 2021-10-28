package com.dtflys.forest.backend.httpclient.body;

import com.dtflys.forest.backend.body.AbstractBodyBuilder;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.converter.protobuf.ForestProtobufConverter;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.http.body.SupportFormUrlEncoded;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.multipart.ForestMultipart;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.RequestNameValue;
import com.dtflys.forest.utils.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
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
        if (StringUtils.isNotEmpty(charset) && mergeCharset) {
            if (!contentType.contains("charset=")) {
                contentType = contentType + "; charset=" + charset.toLowerCase();
            } else {
                String[] strs = contentType.split("charset=");
                contentType = strs[0] + " charset=" + charset.toLowerCase();
            }
            entity.setContentEncoding(charset);
        }
        entity.setContentType(contentType);
        httpReq.setEntity(entity);
    }


    @Override
    protected void setFormBody(T httpReq, ForestRequest request, String charset, String contentType, List<ForestRequestBody> bodyItems) {
        ForestJsonConverter jsonConverter = request.getConfiguration().getJsonConverter();
        List<RequestNameValue> nameValueList = new LinkedList<>();
        for (ForestRequestBody bodyItem : bodyItems) {
            if (bodyItem instanceof SupportFormUrlEncoded) {
                nameValueList.addAll(((SupportFormUrlEncoded) bodyItem).getNameValueList(request.getConfiguration()));
            }
        }
        List<NameValuePair> nameValuePairs = new ArrayList<>(nameValueList.size());
        nameValueList = processFromNameValueList(nameValueList, request.getConfiguration());
        for (int i = 0; i < nameValueList.size(); i++) {
            RequestNameValue nameValue = nameValueList.get(i);
            if (!nameValue.isInBody()) {
                continue;
            }
            String name = nameValue.getName();
            Object value = nameValue.getValue();
            NameValuePair nameValuePair = new BasicNameValuePair(name, MappingTemplate.getFormValueString(jsonConverter, value));
            nameValuePairs.add(nameValuePair);
        }

        try {
            StringEntity entity = new UrlEncodedFormEntity(nameValuePairs, charset);
            if (StringUtils.isNotEmpty(contentType)) {
                entity.setContentType(contentType);
            }
            httpReq.setEntity(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setFileBody(T httpReq,
                               ForestRequest request,
                               String charset,
                               String contentType,
                               List<RequestNameValue> nameValueList,
                               List<ForestMultipart> multiparts,
                               LifeCycleHandler lifeCycleHandler) {
        String boundary = request.getBoundary();
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        if (StringUtils.isNotEmpty(boundary)) {
            entityBuilder.setBoundary(boundary);
        }
        // 解决文件名乱码问题
        ForestJsonConverter jsonConverter = request.getConfiguration().getJsonConverter();
        Charset httpCharset = Charset.forName(charset);
        Charset itemCharset = StandardCharsets.UTF_8;
        if (StringUtils.isNotEmpty(charset)) {
            itemCharset = Charset.forName(charset);
        }
        if (!nameValueList.isEmpty()) {
            entityBuilder.setCharset(httpCharset);
            entityBuilder.setMode(HttpMultipartMode.RFC6532);
        }

        for (RequestNameValue nameValue : nameValueList) {
            if (!nameValue.isInBody()) {
                continue;
            }
            String name = nameValue.getName();
            Object value = nameValue.getValue();
            String text = MappingTemplate.getParameterValue(jsonConverter, value);
            String partContentType = nameValue.getPartContentType();
            if (StringUtils.isEmpty(partContentType)) {
                partContentType = "text/plain";
            }
            ContentType itemContentType = ContentType.create(partContentType, itemCharset);
            entityBuilder.addTextBody(name, text, itemContentType);
        }
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
//        if (httpReq.getFirstHeader("Content-Type") != null) {
//            httpReq.removeHeaders("Content-Type");
//        }
//        httpReq.addHeader("Content-Type", com.dtflys.forest.backend.ContentType.MULTIPART_FORM_DATA);
        HttpEntity entity = entityBuilder.build();
        httpReq.setEntity(entity);
    }


    @Override
    protected void setBinaryBody(T httpReq,
                                 ForestRequest request,
                                 String charset,
                                 String contentType,
                                 byte[] bytes) {

        if (StringUtils.isBlank(contentType)) {
            contentType = ContentType.APPLICATION_OCTET_STREAM.toString();
        }
        ContentType ctype = ContentType.create(contentType, charset);
        HttpEntity entity = new ByteArrayEntity(bytes, ctype);
        httpReq.setEntity(entity);
    }

    @Override
    protected void setProtobuf(T httpReq, ForestRequest request, String charset, String contentType, List<RequestNameValue> nameValueList, Object source) {
        ForestProtobufConverter converter = request.getConfiguration().getProtobufConverter();
        request.getConfiguration().getConverterMap().computeIfAbsent(ForestDataType.PROTOBUF,v -> converter);
        byte[] bytes = converter.convertToByte(source);
        ByteArrayEntity byteArrayEntity = new ByteArrayEntity(bytes);
        byteArrayEntity.setContentType(contentType);
        httpReq.setEntity(byteArrayEntity);
    }

}
