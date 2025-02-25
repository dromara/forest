package com.dtflys.forest.sse;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestSSE;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.TypeReference;

/**
 * Forest SSE 事件来源
 *
 * @since 1.6.0
 */
public class EventSource {
    
    private final ForestSSE sse;
    
    private final SSEEventList eventList;

    private final String name;

    private final ForestRequest request;

    private final ForestResponse response;

    private final String rawData;

    private final String value;

    private volatile SSEMessageResult messageResult = SSEMessageResult.PROCEED;

    public EventSource(SSEEventList eventList, ForestSSE sse, String name, ForestRequest request, ForestResponse response) {
        this(eventList, sse, name, request, response, null, null);
    }

    public EventSource(SSEEventList eventList, ForestSSE sse, String name, ForestRequest request, ForestResponse response, String rawData, String value) {
        this.eventList = eventList == null ? new SSEEventList(sse, request, response) : eventList;
        this.sse = sse;
        this.name = name;
        this.request = request;
        this.response = response;
        this.rawData = rawData;
        this.value = value;
        this.eventList.addEventSource(this);
    }

    /**
     * 获取当前消息对应的请求对象
     * 
     * @return 请求对象
     */
    public ForestRequest request() {
        return request;
    }

    /**
     * 获取当前消息对应的响应对象
     * 
     * @return 响应对象
     */
    public ForestResponse response() {
        return response;
    }

    /**
     * 获取当前消息的原始数据
     * 
     * @return 字符串的原始数据
     */
    public String rawData() {
        return rawData;
    }

    /**
     * 获取当前消息的消息名称
     * 
     * @return 消息名称
     */
    public String name() {
        return name;
    }

    /**
     * 根据消息行数组下标 index，获取消息行列表中第 index 个消息的消息名称
     * <p>在多行模式下，一个 SSE 消息，包含多个消息行</p>
     * 
     * @param index 消息行下标
     * @return 消息名称
     * @since 1.6.4
     */
    public String name(int index) {
        return eventList.name(index);
    }

    /**
     * 获取消息行列表
     * <p>在多行模式下，一个 SSE 消息，包含多个消息行</p>
     * 
     * @return
     */
    public SSEEventList list() {
        return eventList;
    }

    /**
     * 根据消息行数组下标 index，获取消息行列表中第 index 个消息的消息
     * <p>在多行模式下，一个 SSE 消息，包含多个消息行</p>
     * 
     * @param index 消息行下标
     * @return SSE 消息
     * @since 1.6.4
     */
    public EventSource get(int index) {
        return eventList.get(index);
    }

    /**
     * 根据消息行数组下标 index，获取消息行列表中第 index 个消息的消息值
     * <p>在多行模式下，一个 SSE 消息，包含多个消息行</p>
     * 
     * @param index 消息行下标
     * @return 字符串类型的消息值
     * @since 1.6.4
     */
    public String value(int index) {
        return eventList.value(index);
    }

    /**
     * 根据消息行数组下标 index，获取消息行列表中第 index 个消息的消息值，并转换为对应的类型
     * <p>在多行模式下，一个 SSE 消息，包含多个消息行</p>
     * 
     * @param index 消息行下标
     * @param type 类型，Class对象
     * @return 转换后的消息值
     * @param <T> 消息值类型泛型
     * @since 1.6.4
     */
    public <T> T value(int index, Class<T> type) {
        return eventList.value(index, type);
    }

    /**
     * 根据消息行数组下标 index，获取消息行列表中第 index 个消息的消息值，并转换为对应的类型
     * <p>在多行模式下，一个 SSE 消息，包含多个消息行</p>
     * 
     * @param index 消息行下标
     * @param type 类型引用
     * @return 转换后的消息值
     * @param <T> 消息值类型泛型
     * @since 1.6.4
     */
    public <T> T value(int index, TypeReference<T> type) {
        return eventList.value(index, type);
    }

    /**
     * 获取当前消息的消息值
     * 
     * @return 字符串类型的消息值
     */
    public String value() {
        return value;
    }

    /**
     * 获取当前消息的消息值，并转换为对应的类型
     * 
     * @param type 类型，Class对象
     * @return 转换后的消息值
     * @param <T> 消息值类型泛型
     */
    public <T> T value(Class<T> type) {
        T encodedValue = (T) request.getConfiguration().getConverter(ForestDataType.AUTO).convertToJavaObject(value, type);
        return encodedValue;
    }

    /**
     * 获取当前消息的消息值，并转换为对应的类型
     *
     * @param typeReference 类型引用
     * @return 转换后的消息值
     * @param <T> 消息值类型泛型
     */
    public <T> T value(TypeReference<T> typeReference) {
        T encodedValue = (T) request.getConfiguration().getConverter(ForestDataType.AUTO).convertToJavaObject(value, typeReference);
        return encodedValue;
    }

    /**
     * 根据消息名称获取消息的消息值
     * 
     * @param name 消息名称
     * @return 字符串类型的消息值
     */
    public String value(String name) {
        return this.eventList.value(name);
    }

    /**
     * 根据消息名称获取消息的消息值，并转换为对应的类型
     * 
     * @param name 消息名称
     * @param type 类型，Class对象
     * @return 转换后的消息值
     * @param <T> 消息值类型泛型
     * @since 1.6.4
     */
    public <T> T value(String name, Class<T> type) {
        return this.eventList.value(name, type);
    }

    /**
     * 根据消息名称获取消息的消息值，并转换为对应的类型
     * 
     * @param name 消息名称
     * @param typeReference 类型引用
     * @return 转换后的消息值
     * @param <T> 消息值类型泛型
     * @since 1.6.4
     */
    public <T> T value(String name, TypeReference<T> typeReference) {
        return this.eventList.value(name, typeReference);
    }

    /**
     * 获取消息名称为 data 的消息值
     *
     * @return 字符串类型的消息值
     * @since 1.6.4
     */
    public String data() {
        return value("data");
    }

    /**
     * 获取消息名称为 data 的消息值，并转换为对应的类型
     *
     * @param type 类型，Class对象
     * @return 转换后的消息值
     * @param <T> 消息值类型泛型
     */
    public <T> T data(Class<T> type) {
        return value("data", type);
    }

    /**
     * 获取消息名称为 data 的消息值，并转换为对应的类型
     *
     * @param typeReference 类型引用
     * @return 转换后的消息值
     * @param <T> 消息值类型泛型
     */
    public <T> T data(TypeReference<T> typeReference) {
        return value("data", typeReference);
    }

    /**
     * 获取消息名称为 event 的消息值
     *
     * @return 字符串类型的消息值
     * @since 1.6.4
     */
    public String event() {
        return value("event");
    }

    /**
     * 获取消息名称为 event 的消息值，并转换为对应的类型
     *
     * @param type 类型，Class对象
     * @return 转换后的消息值
     * @param <T> 消息值类型泛型
     */
    public <T> T event(Class<T> type) {
        return value("event", type);
    }

    /**
     * 获取消息名称为 event 的消息值，并转换为对应的类型
     *
     * @param typeReference 类型引用
     * @return 转换后的消息值
     * @param <T> 消息值类型泛型
     */
    public <T> T event(TypeReference<T> typeReference) {
        return value("event", typeReference);
    }

    /**
     * 获取消息名称为 id 的消息值
     * 
     * @return 字符串类型的消息值
     * @since 1.6.4
     */
    public String id() {
        return value("id");
    }

    /**
     * 获取消息名称为 id 的消息值，并转换为对应的类型
     * 
     * @param type 类型，Class对象
     * @return 转换后的消息值
     * @param <T> 消息值类型泛型
     */
    public <T> T id(Class<T> type) {
        return value("id", type);
    }

    /**
     * 获取消息名称为 id 的消息值，并转换为对应的类型
     * 
     * @param typeReference 类型引用
     * @return 转换后的消息值
     * @param <T> 消息值类型泛型
     */
    public <T> T id(TypeReference<T> typeReference) {
        return value("id", typeReference);
    }

    public SSEMessageResult messageResult() {
        return messageResult;
    }

    public ForestSSE sse() {
        return sse;
    }

    public EventSource close() {
        sse.close();
        this.messageResult = SSEMessageResult.CLOSE;
        return this;
    }
    
}
