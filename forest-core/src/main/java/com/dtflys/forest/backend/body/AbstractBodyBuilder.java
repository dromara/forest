package com.dtflys.forest.backend.body;

import com.dtflys.forest.backend.BodyBuilder;
import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.converter.form.ForestFormConvertor;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.converter.protobuf.ForestProtobufConverter;
import com.dtflys.forest.converter.text.DefaultTextConverter;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestBody;
import com.dtflys.forest.http.ForestBodyType;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.multipart.ForestMultipart;
import com.dtflys.forest.utils.RequestNameValue;
import com.dtflys.forest.utils.StringUtils;
import scala.Char;

import java.nio.charset.Charset;
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
        boolean mergeCharset = false;
        Charset charset = null;
        if (StringUtils.isEmpty(strCharset)) {
            if (typeGroup.length > 1) {
                strCharset = typeGroup[1];
                mergeCharset = true;
                charset = Charset.forName(strCharset);
            } else {
                strCharset = "UTF-8";
            }
        } else {
            charset = Charset.forName(strCharset);
        }

        if (StringUtils.isEmpty(mineType)) {
            mineType = ContentType.APPLICATION_X_WWW_FORM_URLENCODED;
        }
        List<RequestNameValue> nameValueList = request.getDataNameValueList();

        ContentType mineContentType = new ContentType(mineType);

        ForestBody reqBody = request.getBody();
        boolean needRequestBody = request.getType().isNeedBody() ||
                !reqBody.isEmpty() ||
                !request.getMultiparts().isEmpty();

        if (needRequestBody) {
            ForestBodyType bodyType = request.bodyType();
            if (bodyType == null) {
                bodyType = mineContentType.bodyType();
            }
            if (bodyType == ForestBodyType.FORM) {
                ForestFormConvertor forestFormConvertor = new ForestFormConvertor(request.getConfiguration());
                byte[] bodyBytes = forestFormConvertor.encodeRequestBody(reqBody, charset);
                setBinaryBody(httpRequest, request, strCharset, contentType, bodyBytes);
            } else if(bodyType == ForestBodyType.PROTOBUF) {
                ForestProtobufConverter protobufConverter = request.getConfiguration().getProtobufConverter();
                byte[] bodyBytes = protobufConverter.encodeRequestBody(reqBody, charset);
                setBinaryBody(httpRequest, request, strCharset, contentType, bodyBytes);
            } else if (bodyType == ForestBodyType.JSON) {
                ForestJsonConverter jsonConverter = request.getConfiguration().getJsonConverter();
                byte[] bytes = jsonConverter.encodeRequestBody(reqBody, charset);
                setBody(httpRequest, request, bytes, charset, contentType, mergeCharset);
            } else if (bodyType == ForestBodyType.MULTIPART) {
                List<ForestMultipart> multiparts = request.getMultiparts();
                setFileBody(httpRequest, request, charset, contentType, nameValueList, multiparts, lifeCycleHandler);
            } else if (bodyType == ForestBodyType.BINARY) {
                List<ForestMultipart> multiparts = request.getMultiparts();
                List<byte[]> byteList = new LinkedList<>();
                int size = 0;
                for (ForestMultipart multipart : multiparts) {
                    byte[] byteArray = multipart.getBytes();
                    byteList.add(byteArray);
                    size += byteArray.length;
                }
                for (ForestRequestBody body : reqBody) {
                    byte[] byteArray = body.getByteArray();
                    byteList.add(byteArray);
                    size += byteArray.length;
                }
                byte[] bytes = new byte[size];
                int pos = 0;
                for (byte[] bytesItem : byteList) {
                    for (int i = 0; i < bytesItem.length; i++) {
                        bytes[pos + i] = bytesItem[i];
                    }
                    pos += bytesItem.length;
                }
                setBinaryBody(httpRequest, request, strCharset, contentType, bytes);
            } else {
                byte[] bodyBytes = textConverter.encodeRequestBody(reqBody, charset);
                setBinaryBody(httpRequest, request, strCharset, contentType, bodyBytes);
            }
        }
    }

    public void setBody(T httpReq, ForestRequest request, byte[] bytes, Charset charset, String contentType, boolean mergeCharset) {
        if (charset != null) {
            String text = new String(bytes, charset);
            setStringBody(httpReq, request, text, charset.name(), contentType, mergeCharset);
        } else {
            setBinaryBody(httpReq, request, charset.name(), contentType, bytes);
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
     * @param nameValueList 键值对列表
     * @param multiparts Multiparts
     * @param lifeCycleHandler 生命周期处理器
     */
    protected abstract void setFileBody(T httpReq, ForestRequest request, Charset charset, String contentType, List<RequestNameValue> nameValueList, List<ForestMultipart> multiparts, LifeCycleHandler lifeCycleHandler);

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
                                 String charset,
                                 String contentType,
                                 byte[] bytes);



}
