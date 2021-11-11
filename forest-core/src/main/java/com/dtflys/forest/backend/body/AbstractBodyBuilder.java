package com.dtflys.forest.backend.body;

import com.dtflys.forest.backend.BodyBuilder;
import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.converter.ForestEncoder;
import com.dtflys.forest.converter.text.DefaultTextConverter;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestBody;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.multipart.ForestMultipart;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.RequestNameValue;
import com.dtflys.forest.utils.StringUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 * 通用的请求体构造器抽象类
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-27 18:06
 */
public abstract class AbstractBodyBuilder<T> implements BodyBuilder<T> {

    private static final DefaultTextConverter textConverter = new DefaultTextConverter();

    /**
     * 构建请求体
     * @param httpRequest 后端http请求对象
     * @param request Forest请求对象
     * @param lifeCycleHandler 生命周期处理器
     */
    @Override
    public void buildBody(T httpRequest, ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        String contentType = request.getContentType();

        if (StringUtils.isEmpty(contentType)) {
            contentType = ContentType.APPLICATION_X_WWW_FORM_URLENCODED;
        }

        String[] typeGroup = contentType.split(";[ ]*charset=");
        String mineType = typeGroup[0];
        String strCharset = request.getCharset();
        Charset charset = null;
        boolean mergeCharset = typeGroup.length > 1;
        if (StringUtils.isEmpty(strCharset)) {
            if (typeGroup.length > 1) {
                strCharset = typeGroup[1];
                charset = Charset.forName(strCharset);
            } else {
                charset = StandardCharsets.UTF_8;
            }
        } else {
            charset = Charset.forName(strCharset);
        }



        if (StringUtils.isEmpty(mineType)) {
            mineType = ContentType.APPLICATION_X_WWW_FORM_URLENCODED;
        }


        ContentType mineContentType = new ContentType(mineType);
        String ctypeWithoutParams = mineContentType.toStringWithoutParameters();

        ForestEncoder encoder = request.getEncoder();
        if (encoder != null) {
            byte[] bodyBytes = encoder.encodeRequestBody(request, charset);
            setBinaryBody(httpRequest, request, charset, ctypeWithoutParams, bodyBytes, mergeCharset);
            return;
        }


        ForestBody reqBody = request.getBody();
        boolean needRequestBody = request.getType().isNeedBody() ||
                !reqBody.isEmpty() ||
                !request.getMultiparts().isEmpty();

        if (needRequestBody) {
            ForestDataType bodyType = request.bodyType();
            if (bodyType == null || bodyType == ForestDataType.AUTO) {
                bodyType = mineContentType.bodyType();
            }
            if (bodyType == ForestDataType.MULTIPART) {
                setFileBody(httpRequest, request, charset, ctypeWithoutParams, lifeCycleHandler);
                return;
            }
            ForestEncoder bodyEncoder = (ForestEncoder) request.getConfiguration().getConverterMap().get(bodyType);
            if (bodyEncoder == null) {
                bodyEncoder = (ForestEncoder) request.getConfiguration().getConverterMap().get(ForestDataType.TEXT);
            }
            byte[] bodyBytes = bodyEncoder.encodeRequestBody(request, charset);
            setBinaryBody(httpRequest, request, charset, ctypeWithoutParams, bodyBytes, mergeCharset);
        }
    }

    public void setBody(T httpReq, ForestRequest request, byte[] bytes, Charset charset, String contentType, boolean mergeCharset) {
        if (charset != null) {
            String text = new String(bytes, charset);
            setStringBody(httpReq, request, text, charset, contentType, mergeCharset);
        } else {
            setBinaryBody(httpReq, request, charset, contentType, bytes, mergeCharset);
        }
    }

    /**
     * 设置字符串请求体
     * @param httpReq 后端请求对象
     * @param request Forest请求对象
     * @param text 字符串文本
     * @param charset 字符集
     * @param contentType 数据类型
     * @param mergeCharset 是否合并字符集
     */
    protected abstract void setStringBody(T httpReq, ForestRequest request, String text, Charset charset, String contentType, boolean mergeCharset);


    /**
     * 设置文件请求体
     * @param httpReq 后端请求对象
     * @param request Forest请求对象
     * @param charset 字符集
     * @param contentType 数据类型
     * @param lifeCycleHandler 生命周期处理器
     */
    protected abstract void setFileBody(T httpReq, ForestRequest request, Charset charset, String contentType, LifeCycleHandler lifeCycleHandler);

    /**
     * 设置二进制请求体
     * @param httpReq 后端请求对象
     * @param request Forest请求对象
     * @param charset 字符集
     * @param contentType 数据类型
     * @param bytes 字节数组
     */
    protected abstract void setBinaryBody(T httpReq,
                                 ForestRequest request,
                                 Charset charset,
                                 String contentType,
                                 byte[] bytes,
                                 boolean mergeCharset);



}
