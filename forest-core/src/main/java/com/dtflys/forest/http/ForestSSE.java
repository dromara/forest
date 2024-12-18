package com.dtflys.forest.http;

import cn.hutool.core.collection.CollectionUtil;
import com.dtflys.forest.annotation.SSEDataMessage;
import com.dtflys.forest.annotation.SSEEventMessage;
import com.dtflys.forest.annotation.SSEIdMessage;
import com.dtflys.forest.annotation.SSEMessage;
import com.dtflys.forest.annotation.SSEName;
import com.dtflys.forest.annotation.SSERetryMessage;
import com.dtflys.forest.annotation.SSEValue;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.reflection.MethodLifeCycleHandler;
import com.dtflys.forest.sse.EventSource;
import com.dtflys.forest.sse.ForestSSEListener;
import com.dtflys.forest.sse.SSEMessageConsumer;
import com.dtflys.forest.sse.SSEMessageResult;
import com.dtflys.forest.sse.SSEStringMessageConsumer;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.ReflectUtils;
import com.dtflys.forest.utils.StringUtils;
import com.dtflys.forest.utils.TypeReference;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Forest SSE 控制器
 *
 * @since 1.6.0
 */
public class ForestSSE implements ForestSSEListener<ForestSSE> {

    private ForestRequest<InputStream> request;

    private Consumer<EventSource> onOpenConsumer;

    private BiConsumer<ForestRequest, ForestResponse> onCloseConsumer;

    private Map<String, List<SSEStringMessageConsumer>> consumerMap = new ConcurrentHashMap<>();


    public static ForestSSE fromRequest(ForestRequest request) {
        ForestSSE sse = new ForestSSE();
        sse.init(request);
        return sse;
    }

    public static <T extends ForestSSE> T fromClass(ForestRequest request, Class<T> clazz) {
        T sse = request.getConfiguration().getForestObject(clazz);
        sse.init(request);
        return sse;
    }

    protected ForestSSE() {}

    /**
     * 初始化 SSE 控制器
     * 
     * @param request Forest 请求对象
     * @since 1.6.0
     */
    void init(final ForestRequest request) {
        if (this.request == null) {
            this.request = request;
            this.request.isSSE = true;
            this.request.setLifeCycleHandler(new MethodLifeCycleHandler<InputStream>(InputStream.class, InputStream.class) {});
            final Class<?> clazz = this.getClass();
            final Method[] methods = ReflectUtils.getMethods(clazz);
            
            // 批量注册 SSE 控制器类中的消息处理方法
            for (final Method method : methods) {
                final Annotation[] annArray = method.getAnnotations();
                for (final Annotation ann : annArray) {
                    if (ann instanceof SSEMessage) {
                        registerMessageMethod(request, method, ann, null);
                    } else if (ann instanceof SSEDataMessage) {
                        registerMessageMethod(request, method, ann, "data");
                    } else if (ann instanceof SSEEventMessage) {
                        registerMessageMethod(request, method, ann, "event");
                    } else if (ann instanceof SSEIdMessage) {
                        registerMessageMethod(request, method, ann, "id");
                    } else if (ann instanceof SSERetryMessage) {
                        registerMessageMethod(request, method, ann, "retry");
                    }
                }
            }
        }
    }

    /**
     * 注册 SSE 消息处理方法
     * 
     * @param request Forest 请求对象
     * @param method Java 方法对象
     * @param ann 注解对象
     * @param defaultName SSE 消息默认名称
     * @since 1.6.0
     */
    private void registerMessageMethod(ForestRequest request, Method method, Annotation ann, String defaultName) {
        Map<String, Object> attrs = ReflectUtils.getAttributesFromAnnotation(ann);
        String valueRegex = String.valueOf(attrs.getOrDefault("valueRegex", ""));
        String valuePrefix = String.valueOf(attrs.getOrDefault("valuePrefix", ""));
        String valuePostfix = String.valueOf(attrs.getOrDefault("valuePostfix", ""));
        String annName = defaultName != null ? defaultName : String.valueOf(attrs.getOrDefault("name", ""));
        if (StringUtils.isEmpty(valueRegex) && StringUtils.isEmpty(valuePrefix) && StringUtils.isEmpty(valuePostfix)) {
            addConsumer(annName, (eventSource, name, value) -> callSSEMessageMethod(method, eventSource, name, value, request, eventSource.getResponse()));
        } else {
            addConsumerMatches(annName, valueRegex, valuePrefix, valuePostfix,
                    (eventSource, name, value) -> callSSEMessageMethod(method, eventSource, name, value, request, eventSource.getResponse()));
        }
    }

    /**
     * 调用 SSE 消息处理方法
     * 
     * @param method Java 方法对象
     * @param eventSource SSE 消息源
     * @param name 消息的名称
     * @param value 消息的值
     * @param request Forest 请求对象
     * @param response Forest 响应对象
     * @since 1.6.0
     */
    private void callSSEMessageMethod(
            final Method method,
            final EventSource eventSource,
            final String name,
            final String value,
            ForestRequest request,
            ForestResponse response) {
        final Class<?>[] paramTypes = method.getParameterTypes();
        final Object[] args = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> paramType = paramTypes[i];
            if (EventSource.class.isAssignableFrom(paramType)) {
                args[i] = eventSource;
            } else if (ForestRequest.class.isAssignableFrom(paramType)) {
                args[i] = request;
            } else if (ForestResponse.class.isAssignableFrom(paramType)) {
                args[i] = response;
            } else {
                final Annotation[] paramAnnArray = paramType.getAnnotations();
                if (paramAnnArray.length > 0) {
                    for (final Annotation ann : paramAnnArray) {
                        if (ann instanceof SSEName) {
                            args[i] = name;
                        } else if (ann instanceof SSEValue) {
                            setParameterValue(method, value, request, args, i, paramType);
                        }
                    }
                } else {
                    setParameterValue(method, value, request, args, i, paramType);
                }
            }
        }
        final boolean accessible = method.isAccessible();
        method.setAccessible(true);
        try {
            method.invoke(this, args);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new ForestRuntimeException(e);
        } finally {
            method.setAccessible(accessible);
        }
    }

    private void setParameterValue(Method method, String value, ForestRequest request, Object[] args, int i, Class<?> paramType) {
        if (CharSequence.class.isAssignableFrom(paramType)) {
            args[i] = value;
        } else {
            final Type type = method.getParameters()[i].getParameterizedType();
            final Object encodedValue = request.getConfiguration().getConverter(ForestDataType.AUTO).convertToJavaObject(value, type);
            args[i] = encodedValue;
        }
    }

    /**
     * 添加字符串类型 SSE 消费者
     * 
     * @param name 消息名称
     * @param consumer 字符串类型 SSE 消息消费者
     * @return SSE 控制器自身对象
     * @since 1.6.0
     */
    public ForestSSE addConsumer(String name, SSEStringMessageConsumer consumer) {
        consumerMap.computeIfAbsent(name, k -> new CopyOnWriteArrayList<>()).add(consumer);
        return this;
    }

    /**
     * 添加通用类型 SSE 消费者
     * 
     * @param name 消息名称
     * @param valueType 值的类型: {@link Class}对象
     * @param consumer 通用类型 SSE 消息消费者
     * @return SSE 控制器自身对象
     * @param <T> 值的类型
     * @since 1.6.0
     */
    public <T> ForestSSE addConsumer(String name, Class<T> valueType, SSEMessageConsumer<T> consumer) {
        consumerMap.computeIfAbsent(name, k -> new CopyOnWriteArrayList<>()).add((eventSource, n, v) -> {
            if (valueType.isAssignableFrom(CharSequence.class)) {
                consumer.onMessage(eventSource, n, (T) String.valueOf(v));
            } else {
                T encodedValue = (T) request.getConfiguration().getConverter(ForestDataType.AUTO).convertToJavaObject(v, valueType);
                consumer.onMessage(eventSource, n, encodedValue);
            }
        });
        return this;
    }

    /**
     * 添加通用类型 SSE 消费者
     * 
     * @param name 消息名称
     * @param valueType 值的类型: {@link TypeReference}对象
     * @param consumer 通用类型 SSE 消息消费者
     * @return SSE 控制器自身对象
     * @param <T> 值的类型
     * @since 1.6.0
     */
    public <T> ForestSSE addConsumer(String name, TypeReference<T> valueType, SSEMessageConsumer<T> consumer) {
        consumerMap.computeIfAbsent(name, k -> new CopyOnWriteArrayList<>()).add((eventSource, n, v) -> {
            T encodedValue = (T) request.getConfiguration().getConverter(ForestDataType.AUTO).convertToJavaObject(v, valueType);
            consumer.onMessage(eventSource, n, encodedValue);
        });
        return this;
    }


    /**
     * 添加带模式匹配的字符串类型 SSE 消费者
     * 
     * @param name 消息名称
     * @param matcher 模式匹配函数
     * @param consumer 字符串类型 SSE 消息消费者
     * @return SSE 控制器自身对象
     * @since 1.6.0
     */
    public ForestSSE addConsumer(String name, Function<EventSource, Boolean> matcher, SSEStringMessageConsumer consumer) {
        consumerMap.computeIfAbsent(name, k -> new CopyOnWriteArrayList<>()).add(new SSEStringMessageConsumer() {
            @Override
            public void onMessage(EventSource eventSource, String name, String value) {
                consumer.onMessage(eventSource, name, value);
            }

            @Override
            public boolean matches(EventSource eventSource) {
                return matcher.apply(eventSource);
            }
        });
        return this;
    }

    /**
     * 添加匹配前缀的字符串类型 SSE 消费者
     * 
     * @param name 消息名称
     * @param valuePrefix 值的前缀
     * @param consumer 字符串类型 SSE 消息消费者
     * @return SSE 控制器自身对象
     * @since 1.6.0
     */
    public ForestSSE addConsumerMatchesPrefix(String name, String valuePrefix, SSEStringMessageConsumer consumer) {
        return addConsumer(name, eventSource -> eventSource.getValue().startsWith(valuePrefix), consumer);
    }

    /**
     * 添加匹配后缀的字符串类型 SSE 消费者
     * 
     * @param name 消息名称
     * @param valuePostfix 值的后缀
     * @param consumer 字符串类型 SSE 消息消费者
     * @return SSE 控制器自身对象
     * @since 1.6.0
     */
    public ForestSSE addConsumerMatchesPostfix(String name, String valuePostfix, SSEStringMessageConsumer consumer) {
        return addConsumer(name, eventSource -> eventSource.getValue().endsWith(valuePostfix), consumer);
    }

    /**
     * 添加带正则匹配的字符串类型 SSE 消费者
     * 
     * @param name 消息名称
     * @param valueRegex 正则表达式
     * @param consumer 字符串类型 SSE 消息消费者
     * @return SSE 控制器自身对象
     * @since 1.6.0
     */
    public ForestSSE addConsumerMatches(String name, String valueRegex, SSEStringMessageConsumer consumer) {
        return addConsumer(name, eventSource -> eventSource.getValue().matches(valueRegex), consumer);
    }

    /**
     * 添加带正则匹配、前缀匹配、以及后缀匹配的字符串类型 SSE 消费者
     * 
     * @param name 消息名称
     * @param valueRegex 正则表达式
     * @param valuePrefix 值的前缀
     * @param valuePostfix 值的后缀
     * @param consumer 字符串类型 SSE 消息消费者
     * @return SSE 控制器自身对象
     * @since 1.6.0
     */
    public ForestSSE addConsumerMatches(String name, String valueRegex, String valuePrefix, String valuePostfix, SSEStringMessageConsumer consumer) {
        return addConsumer(name, eventSource -> {
            final String value = eventSource.getValue();
            if (StringUtils.isNotEmpty(valueRegex)) {
                if (!value.matches(valueRegex)) {
                    return false;
                }
            }
            if (StringUtils.isNotEmpty(valuePrefix)) {
                if (!value.startsWith(valuePrefix)) {
                    return false;
                }
            }
            if (StringUtils.isNotEmpty(valuePostfix)) {
                if (!value.endsWith(valuePostfix)) {
                    return false;
                }
            }
            return true;
        }, consumer);
    }

    /**
     * 设置监听打开的回调函数: 该回调函数会在 SSE 开始监听时执行
     * 
     * @param onOpenConsumer 回调函数
     * @return SSE 控制器自身对象
     * @since 1.6.0
     */
    public ForestSSE setOnOpen(Consumer<EventSource> onOpenConsumer) {
        this.onOpenConsumer = onOpenConsumer;
        return this;
    }

    /**
     * 设置监听关闭的回调函数: 该回调函数会在 SSE 结束监听时执行
     * 
     * @param onCloseConsumer 回调函数
     * @return SSE 控制器自身对象
     * @since 1.6.0
     */
    public ForestSSE setOnClose(BiConsumer<ForestRequest, ForestResponse> onCloseConsumer) {
        this.onCloseConsumer = onCloseConsumer;
        return this;
    }

    /**
     * 添加监听 data 事件的回调函数: 该回调函数会在 SSE 监听到 name 为 data 的消息时执行
     * 
     * @param consumer 回调函数
     * @return SSE 控制器自身对象
     * @since 1.6.0
     */
    public ForestSSE addOnData(SSEStringMessageConsumer consumer) {
        return addConsumer("data", consumer);
    }

    /**
     * 添加监听 data 事件的回调函数: 该回调函数会在 SSE 监听到 name 为 data 的消息时执行，并且将值转换为给的类型的对象
     * 
     * @param valueClass 值的类型: {@link Class}对象
     * @param consumer 回调函数
     * @return SSE 控制器自身对象
     * @param <T> 值的类型
     * @since 1.6.0
     */
    public <T> ForestSSE addOnData(Class<T> valueClass, SSEMessageConsumer<T> consumer) {
        return addConsumer("data", valueClass, consumer);
    }

    /**
     * 添加匹配值前缀的监听 data 事件的回调函数: 该回调函数会在 SSE 监听到 name 为 data 的消息，并且值能匹配给定前缀时才会执行
     * 
     * @param valuePrefix 值的前缀
     * @param consumer 回调函数
     * @return SSE 控制器自身对象
     * @since 1.6.0
     */
    public ForestSSE addOnDataMatchesPrefix(String valuePrefix, SSEStringMessageConsumer consumer) {
        return addConsumerMatchesPrefix("data", valuePrefix, consumer);
    }

    /**
     * 添加匹配值后缀的监听 data 事件的回调函数: 该回调函数会在 SSE 监听到 name 为 data 的消息，并且值能匹配给定的后缀时才会执行
     * 
     * @param valuePostfix 值的后缀
     * @param consumer 回调函数
     * @return SSE 控制器自身对象
     * @since 1.6.0
     */
    public ForestSSE addOnDataMatchesPostfix(String valuePostfix, SSEStringMessageConsumer consumer) {
        return addConsumerMatchesPostfix("data", valuePostfix, consumer);
    }

    /**
     * 添加监听 event 事件的回调函数: 该回调函数会在 SSE 监听到 name 为 event 的消息时执行
     * 
     * @param consumer 回调函数
     * @return SSE 控制器自身对象
     * @since 1.6.0
     */
    public ForestSSE addOnEvent(SSEStringMessageConsumer consumer) {
        return addConsumer("event", consumer);
    }

    /**
     * 添加监听 event 事件的回调函数: 该回调函数会在 SSE 监听到 name 为 event 的消息时执行，并且将值转换为给的类型的对象
     * 
     * @param valueClass 值的类型: {@link Class}对象
     * @param consumer 回调函数
     * @return SSE 控制器自身对象
     * @param <T> 值的类型
     * @since 1.6.0
     */
    public <T> ForestSSE addOnEvent(Class<T> valueClass, SSEMessageConsumer<T> consumer) {
        return addConsumer("event", valueClass, consumer);
    }

    /**
     * 添加匹配值前缀的监听 event 事件的回调函数: 该回调函数会在 SSE 监听到 name 为 event 的消息，并且值能匹配给定前缀时才会执行
     *
     * @param valuePrefix 值的前缀
     * @param consumer 回调函数
     * @return SSE 控制器自身对象
     * @since 1.6.0
     */
    public ForestSSE addOnEventMatchesPrefix(String valuePrefix, SSEStringMessageConsumer consumer) {
        return addConsumerMatchesPrefix("event", valuePrefix, consumer);
    }

    /**
     * 添加匹配值后缀的监听 event 事件的回调函数: 该回调函数会在 SSE 监听到 name 为 event 的消息，并且值能匹配给定的后缀时才会执行
     * 
     * @param valuePostfix 值的后缀
     * @param consumer 回调函数
     * @return SSE 控制器自身对象
     * @since 1.6.0
     */
    public ForestSSE addOnEventMatchesPostfix(String valuePostfix, SSEStringMessageConsumer consumer) {
        return addConsumerMatchesPostfix("event", valuePostfix, consumer);
    }

    /**
     * 添加监听 id 事件的回调函数: 该回调函数会在 SSE 监听到 name 为 id 的消息时执行
     * 
     * @param consumer 回调函数
     * @return SSE 控制器自身对象
     * @since 1.6.0
     */
    public ForestSSE addOnId(SSEStringMessageConsumer consumer) {
        return addConsumer("id", consumer);
    }

    /**
     * 添加监听 id 事件的回调函数: 该回调函数会在 SSE 监听到 name 为 id 的消息时执行，并且将值转换为给的类型的对象
     *
     * @param valueClass 值的类型: {@link Class}对象
     * @param consumer 回调函数
     * @return SSE 控制器自身对象
     * @param <T> 值的类型
     * @since 1.6.0
     */
    public <T> ForestSSE addOnId(Class<T> valueClass, SSEMessageConsumer<T> consumer) {
        return addConsumer("id", valueClass, consumer);
    }

    /**
     * 添加匹配值前缀的监听 id 事件的回调函数: 该回调函数会在 SSE 监听到 name 为 id 的消息，并且值能匹配给定前缀时才会执行
     * 
     * @param valuePrefix 值的前缀
     * @param consumer 回调函数
     * @return SSE 控制器自身对象
     * @since 1.6.0
     */
    public ForestSSE addOnIdMatchesPrefix(String valuePrefix, SSEStringMessageConsumer consumer) {
        return addConsumerMatchesPrefix("id", valuePrefix, consumer);
    }

    /**
     * 添加匹配值后缀的监听 id 事件的回调函数: 该回调函数会在 SSE 监听到 name 为 id 的消息，并且值能匹配给定的后缀时才会执行
     * 
     * @param valuePostfix 值的后缀
     * @param consumer 回调函数
     * @return SSE 控制器自身对象
     * @since 1.6.0
     */
    public ForestSSE addOnIdMatchesPostfix(String valuePostfix, SSEStringMessageConsumer consumer) {
        return addConsumerMatchesPostfix("id", valuePostfix, consumer);
    }

    /**
     * 添加监听 retry 事件的回调函数: 该回调函数会在 SSE 监听到 name 为 retry 的消息时执行
     * 
     * @param consumer 回调函数
     * @return SSE 控制器自身对象
     * @since 1.6.0
     */
    public ForestSSE addOnRetry(SSEStringMessageConsumer consumer) {
        return addConsumer("retry", consumer);
    }

    /**
     * 添加监听 retry 事件的回调函数: 该回调函数会在 SSE 监听到 name 为 retry 的消息时执行，并且将值转换为给的类型的对象
     * 
     * @param valueClass 值的类型: {@link Class}对象
     * @param consumer 回调函数
     * @return SSE 控制器自身对象
     * @param <T> 值的类型
     * @since 1.6.0
     */
    public <T> ForestSSE addOnRetry(Class<T> valueClass, SSEMessageConsumer<T> consumer) {
        return addConsumer("retry", valueClass, consumer);
    }

    /**
     * 添加匹配值后缀的监听 retry 事件的回调函数: 该回调函数会在 SSE 监听到 name 为 retry 的消息，并且值能匹配给定的后缀时才会执行
     *
     * @param valuePrefix 值的前缀
     * @param consumer 回调函数
     * @return SSE 控制器自身对象
     * @since 1.6.0
     */
    public ForestSSE addOnRetryMatchesPrefix(String valuePrefix, SSEStringMessageConsumer consumer) {
        return addConsumerMatchesPrefix("retry", valuePrefix, consumer);
    }

    /**
     * 添加匹配值后缀的监听 retry 事件的回调函数: 该回调函数会在 SSE 监听到 name 为 retry 的消息，并且值能匹配给定的后缀时才会执行
     * 
     * @param valuePostfix 值的后缀
     * @param consumer 回调函数
     * @return SSE 控制器自身对象
     * @since 1.6.0
     */
    public ForestSSE addOnRetryMatchesPostfix(String valuePostfix, SSEStringMessageConsumer consumer) {
        return addConsumerMatchesPostfix("retry", valuePostfix, consumer);
    }

    /**
     * 监听打开回调函数：在开始 SSE 数据流监听的时候调用
     * 
     * @param eventSource 消息源
     * @since 1.6.0
     */
    protected void onOpen(EventSource eventSource) {
        if (onOpenConsumer != null) {
            onOpenConsumer.accept(eventSource);
        }
    }

    /**
     * 监听关闭回调函数：在结束 SSE 数据流监听的时候调用
     * 
     * @param request Forest 请求对象
     * @param response Forest 响应对象
     * @since 1.6.0
     */
    protected void onClose(ForestRequest request, ForestResponse response) {
        if (onCloseConsumer != null) {
            onCloseConsumer.accept(request, response);
        }
    }

    /**
     * 消息回调函数：在接收到 SSE 消息后调用
     * 
     * @param eventSource 消息源
     * @param name 名称
     * @param value 值
     * @since 1.6.0
     */
    @Override
    public void onMessage(EventSource eventSource, String name, String value) {
        final List<SSEStringMessageConsumer> consumers = consumerMap.get(name);
        if (CollectionUtil.isEmpty(consumers)) {
            return;
        }
        for (final SSEStringMessageConsumer consumer : consumers) {
            if (!consumer.matches(eventSource)) {
                continue;
            }
            consumer.onMessage(eventSource, name, value);
            if (SSEMessageResult.CLOSE.equals(eventSource.getMessageResult())) {
                return;
            }
        }
    }

    /**
     * 获取 Forest 请求对象
     * 
     * @return Forest 请求对象
     * @since 1.6.0
     */
    @Override
    public ForestRequest getRequest() {
        return this.request;
    }

    /**
     * 解析字符串行
     * 
     * @param line 字符串行
     * @return 解析后的字符串
     * @since 1.6.0
     */
    private String parseLine(String line) {
        final String[] group = line.split("\\:", 2);
        if (group.length == 1) {
            return group[0];
        }
        return StringUtils.trimBegin(group[1]);
    }


    /**
     * 解析事件源
     * 
     * @param response Forest 响应对象
     * @param line 字符串行
     * @return {@link EventSource}事件源对象
     */
    private EventSource parseEventSource(ForestResponse response, String line) {
        final String[] group = line.split("\\:", 2);
        if (group.length == 1) {
            return new EventSource("", request, response, line, line);
        }
        final String name = group[0].trim();
        final String data = StringUtils.trimBegin(group[1]);
        return new EventSource(name, request, response, line, data);
    }


    /**
     * 开始对 SSE 数据流进行监听
     * 
     * @return SSE 控制器自身对象
     * @param <R> 自身类型
     * @since 1.6.0
     */
    @Override
    public <R extends ForestSSE> R listen() {
        final boolean isAsync = this.request.isAsync();
        ForestResponse<InputStream> response;
        if (isAsync) {
            try {
                response = this.request.executeAsCompletableFuture(new TypeReference<ForestResponse<InputStream>>() {}).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new ForestRuntimeException(e);
            }
        } else {
            response =  this.request.execute(new TypeReference<ForestResponse<InputStream>>() {});
        }
        if (response == null) {
            return (R) this;
        }
        if (response.isError()) {
            return (R) this;
        }
        final EventSource openEventSource = new EventSource("open", request, response);
        this.onOpen(openEventSource);
        if (SSEMessageResult.CLOSE.equals(openEventSource.getMessageResult())) {
            onClose(request, response);
            return (R) this;
        }
        try {
            final String charset = Optional.ofNullable(response.getCharset()).orElse("UTF-8");
            final InputStream in = response.getInputStream();
            final InputStreamReader isr = new InputStreamReader(in, charset);
            try (final BufferedReader reader = new BufferedReader(isr)) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (StringUtils.isEmpty(line)) {
                        continue;
                    }
                    final EventSource eventSource = parseEventSource(response, line);
                    onMessage(eventSource, eventSource.getName(), eventSource.getValue());
                    if (SSEMessageResult.CLOSE.equals(eventSource.getMessageResult())) {
                        break;
                    }
                }
            } finally {
                onClose(request, response);
            }
        } catch (Exception e) {
            throw new ForestRuntimeException(e);
        }
        return (R) this;
    }

    /**
     * 开始对 SSE 数据流进行异步监听
     * 
     * @return SSE 控制器自身对象
     * @param <R> 自身类型
     * @since 1.6.0
     */
    @Override
    public <R extends ForestSSE> CompletableFuture<R> asyncListen() {
        return CompletableFuture.supplyAsync(this::listen);
    }

    /**
     * 开始对 SSE 数据流在线程池中进行异步监听
     * 
     * @param pool 线程池
     * @return SSE 控制器自身对象
     * @param <R> 自身类型
     * @since 1.6.0
     */
    @Override
    public <R extends ForestSSE> CompletableFuture<R> asyncListen(ExecutorService pool) {
        return CompletableFuture.supplyAsync(this::listen, pool);
    }
}
