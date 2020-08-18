package com.dtflys.forest.backend.httpclient.body;

import com.dtflys.forest.backend.body.AbstractBodyBuilder;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.multipart.ForestMultipart;
import com.dtflys.forest.utils.RequestNameValue;
import com.dtflys.forest.utils.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * HttpClient后端的请求Body构造器
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 14:52
 */
public class HttpclientBodyBuilder<T extends HttpEntityEnclosingRequestBase> extends AbstractBodyBuilder<T> {


    protected void setStringBody(T httpReq, String text, String charset, String contentType) {
            StringEntity entity = new StringEntity(text, charset);
            if (StringUtils.isNotEmpty(charset)) {
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

    protected void setFormBody(T httpReq, ForestRequest request, String charset, String contentType, List<RequestNameValue> nameValueList) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        ForestJsonConverter jsonConverter = request.getConfiguration().getJsonConverter();
        for (int i = 0; i < nameValueList.size(); i++) {
            RequestNameValue nameValue = nameValueList.get(i);
            if (!nameValue.isInBody()) continue;
            String name = nameValue.getName();
            Object value = nameValue.getValue();
            NameValuePair nameValuePair = new BasicNameValuePair(name, MappingTemplate.getParameterValue(jsonConverter, value));
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
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        // 解决文件名乱码问题
        Charset httpCharset = Charset.forName(charset);
        entityBuilder.setCharset(httpCharset);
        entityBuilder.setMode(HttpMultipartMode.RFC6532);
        ForestJsonConverter jsonConverter = request.getConfiguration().getJsonConverter();
        for (int i = 0; i < nameValueList.size(); i++) {
            RequestNameValue nameValue = nameValueList.get(i);
            if (!nameValue.isInBody()) continue;
            String name = nameValue.getName();
            Object value = nameValue.getValue();
            entityBuilder.addTextBody(name, MappingTemplate.getParameterValue(jsonConverter, value));
        }

        for (int i = 0; i < multiparts.size(); i++) {
            ForestMultipart multipart = multiparts.get(i);
            String name = multipart.getName();
            String fileName = multipart.getOriginalFileName();
            ContentType ctype = ContentType.create(multipart.getContentType(), httpCharset);
            AbstractContentBody contentBody = null;
            if (multipart.isFile()) {
                contentBody = new HttpclientMultipartFileBody(request, multipart.getFile(), ctype, fileName, lifeCycleHandler);
//                entityBuilder.addBinaryBody(name, multipart.getFile(), ctype, fileName);
            } else {
                contentBody = new HttpclientMultipartCommonBody(request, multipart, ctype, fileName, lifeCycleHandler);
//                entityBuilder.addBinaryBody(name, multipart.getInputStream(), ctype, fileName);
            }
            entityBuilder.addPart(name, contentBody);
        }
        HttpEntity entity = entityBuilder.build();
        httpReq.setEntity(entity);
    }


}
