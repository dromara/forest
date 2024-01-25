package com.dtflys.forest.backend.body;

import com.dtflys.forest.backend.BodyBuilder;
import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.converter.ForestEncoder;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestBody;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.utils.ForestDataType;

import java.nio.charset.Charset;
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
     * @param request Forest请Cont求对象
     * @param lifeCycleHandler 生命周期处理器
     */
    @Override
    public void buildBody(final T httpRequest, ForestRequest request, final LifeCycleHandler lifeCycleHandler) {
        final ContentType mineContentType = request.mineContentType();
        final Charset charset = mineContentType.getCharset();
        final String charsetName = mineContentType.getCharsetName();
        final boolean mergeCharset = mineContentType.isHasDefinedCharset();
        final String ctypeWithoutParams = mineContentType.toStringWithoutParameters();
        final ForestEncoder encoder = request.getEncoder();

        if (encoder != null) {
            final byte[] bodyBytes = request.body().encode(encoder, charset);
            final byte[] handledBodyBytes = lifeCycleHandler.handleBodyEncode(request, encoder, bodyBytes);
            setBinaryBody(httpRequest, request, charsetName, ctypeWithoutParams, handledBodyBytes, mergeCharset);
            return;
        }

        final ForestBody reqBody = request.getBody();
        final boolean needRequestBody = request.getType().isNeedBody() ||
                !reqBody.isEmpty() ||
                !request.getMultiparts().isEmpty();

        if (needRequestBody) {
            final ForestDataType bodyType = request.bodyType();
            final ForestDataType dataType = (bodyType == null || bodyType == ForestDataType.AUTO) ? (
                    request.getContentType() == null ? (reqBody.getBodyType() == null ? reqBody.getDefaultBodyType() : reqBody.getBodyType()) : mineContentType.bodyType()
            ) : bodyType;
            if (dataType == ForestDataType.MULTIPART) {
                setFileBody(httpRequest, request, charset, ctypeWithoutParams, lifeCycleHandler);
                return;
            }
            final ForestEncoder bodyEncoder = Optional.ofNullable((ForestEncoder) request.config().getConverterMap().get(dataType)).orElseGet(() ->
                    (ForestEncoder) request.config().getConverterMap().get(ForestDataType.BINARY)
            );
            final byte[] bodyBytes = reqBody.encode(bodyEncoder, charset);
            final byte[] handledBodyBytes = lifeCycleHandler.handleBodyEncode(request, bodyEncoder, bodyBytes);
            setBinaryBody(httpRequest, request, charsetName, ctypeWithoutParams, handledBodyBytes, mergeCharset);
        }
    }

    public void setBody(final T httpReq, ForestRequest request, final byte[] bytes, String charset, final String contentType, final boolean mergeCharset) {
        if (charset != null) {
            final String text = new String(bytes, Charset.forName(charset));
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
    protected abstract void setStringBody(final T httpReq, final ForestRequest request, final String text, final String charset, final String contentType, final boolean mergeCharset);


    /**
     * 设置文件请求体
     * @param httpReq 后端请求对象
     * @param request Forest请求对象
     * @param charset 字符集
     * @param contentType 数据类型
     * @param lifeCycleHandler 生命周期处理器
     */
    protected abstract void setFileBody(final T httpReq, final ForestRequest request, final Charset charset, final String contentType, final LifeCycleHandler lifeCycleHandler);

    /**
     * 设置二进制请求体
     * @param httpReq 后端请求对象
     * @param request Forest请求对象
     * @param charset 字符集
     * @param contentType 数据类型
     * @param bytes 字节数组
     * @param mergeCharset 合并的字符集
     */
    protected abstract void setBinaryBody(final T httpReq,
                                 final ForestRequest request,
                                 final String charset,
                                 String contentType,
                                 final byte[] bytes,
                                 final boolean mergeCharset);



}
