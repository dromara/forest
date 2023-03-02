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

    /**
     * 构建请求体
     * @param httpRequest 后端http请求对象
     * @param request Forest请求对象
     * @param lifeCycleHandler 生命周期处理器
     */
    @Override
    public void buildBody(T httpRequest, ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        ContentType mineContentType = request.mineContentType();
        Charset charset = mineContentType.getCharset();
        String charsetName = mineContentType.getCharsetName();
        boolean mergeCharset = mineContentType.isHasDefinedCharset();
        String ctypeWithoutParams = mineContentType.toStringWithoutParameters();

        ForestEncoder encoder = request.getEncoder();
        if (encoder != null) {
            byte[] bodyBytes = request.body().encode(encoder, charset);
            bodyBytes = lifeCycleHandler.handleBodyEncode(request, encoder, bodyBytes);
            setBinaryBody(httpRequest, request, charsetName, ctypeWithoutParams, bodyBytes, mergeCharset);
            return;
        }


        ForestBody reqBody = request.getBody();
        boolean needRequestBody = request.getType().isNeedBody() ||
                !reqBody.isEmpty() ||
                !request.getMultiparts().isEmpty();

        if (needRequestBody) {
            ForestDataType bodyType = request.bodyType();
            if (bodyType == null || bodyType == ForestDataType.AUTO) {
                if (request.getContentType() == null) {
                    bodyType = reqBody.getBodyType() == null ? reqBody.getDefaultBodyType() : reqBody.getBodyType();
                } else {
                    bodyType = mineContentType.bodyType();
                }
            }
            if (bodyType == ForestDataType.MULTIPART) {
                setFileBody(httpRequest, request, charset, ctypeWithoutParams, lifeCycleHandler);
                return;
            }
            ForestEncoder bodyEncoder = (ForestEncoder) request.getConfiguration().getConverterMap().get(bodyType);
            if (bodyEncoder == null) {
                bodyEncoder = (ForestEncoder) request.getConfiguration().getConverterMap().get(ForestDataType.TEXT);
            }
            byte[] bodyBytes = reqBody.encode(bodyEncoder, charset);
            bodyBytes = lifeCycleHandler.handleBodyEncode(request, bodyEncoder, bodyBytes);
            setBinaryBody(httpRequest, request, charsetName, ctypeWithoutParams, bodyBytes, mergeCharset);
        }
    }

    public void setBody(T httpReq, ForestRequest request, byte[] bytes, String charset, String contentType, boolean mergeCharset) {
        if (charset != null) {
            String text = new String(bytes, Charset.forName(charset));
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
    protected abstract void setStringBody(T httpReq, ForestRequest request, String text, String charset, String contentType, boolean mergeCharset);


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
     * @param mergeCharset 合并的字符集
     */
    protected abstract void setBinaryBody(T httpReq,
                                 ForestRequest request,
                                 String charset,
                                 String contentType,
                                 byte[] bytes,
                                 boolean mergeCharset);



}
