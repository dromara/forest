package com.dtflys.forest.http;

import com.alibaba.fastjson.JSON;
import com.dtflys.forest.annotation.SSEDataMessage;
import com.dtflys.forest.annotation.SSEEventMessage;
import com.dtflys.forest.annotation.SSEIdMessage;
import com.dtflys.forest.annotation.SSEMessage;
import com.dtflys.forest.annotation.SSEName;
import com.dtflys.forest.annotation.SSERetryMessage;
import com.dtflys.forest.annotation.SSEValue;
import com.dtflys.forest.exceptions.ForestConvertException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.sse.SSEMessageConsumer;
import com.dtflys.forest.sse.EventSource;
import com.dtflys.forest.sse.ForestSSEListener;
import com.dtflys.forest.sse.SSEStringMessageConsumer;
import com.dtflys.forest.sse.SSEMessageResult;
import com.dtflys.forest.utils.ForestCache;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.ReflectUtils;
import com.dtflys.forest.utils.StringUtils;
import com.dtflys.forest.utils.TypeReference;
import org.apache.commons.collections4.CollectionUtils;

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
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Forest SSE
 *
 * @since 1.6.0
 */
public class ForestSSE implements ForestSSEListener<ForestSSE> {

    private ForestRequest request;

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

    void init(final ForestRequest request) {
        if (this.request == null) {
            this.request = request;
            this.request.isSSE = true;
            final Class<?> clazz = this.getClass();
            final Method[] methods = ReflectUtils.getMethods(clazz);
            for (final Method method : methods) {
                final Annotation[] annArray = method.getAnnotations();
                for (final Annotation ann : annArray) {
                    if (ann instanceof SSEMessage) {
                        processMessageMethod(request, method, ann, null);
                    } else if (ann instanceof SSEDataMessage) {
                        processMessageMethod(request, method, ann, "data");
                    } else if (ann instanceof SSEEventMessage) {
                        processMessageMethod(request, method, ann, "event");
                    } else if (ann instanceof SSEIdMessage) {
                        processMessageMethod(request, method, ann, "id");
                    } else if (ann instanceof SSERetryMessage) {
                        processMessageMethod(request, method, ann, "retry");
                    }
                }
            }
        }
    }

    private void processMessageMethod(ForestRequest request, Method method, Annotation ann, String defaultName) {
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

    public ForestSSE addConsumer(String name, SSEStringMessageConsumer consumer) {
        consumerMap.computeIfAbsent(name, k -> new CopyOnWriteArrayList<>()).add(consumer);
        return this;
    }


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


    public <T> ForestSSE addConsumer(String name, TypeReference<T> valueType, SSEMessageConsumer<T> consumer) {
        consumerMap.computeIfAbsent(name, k -> new CopyOnWriteArrayList<>()).add((eventSource, n, v) -> {
            T encodedValue = (T) request.getConfiguration().getConverter(ForestDataType.AUTO).convertToJavaObject(v, valueType);
            consumer.onMessage(eventSource, n, encodedValue);
        });
        return this;
    }


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

    public ForestSSE addConsumerMatchesPrefix(String name, String valuePrefix, SSEStringMessageConsumer consumer) {
        return addConsumer(name, eventSource -> eventSource.getValue().startsWith(valuePrefix), consumer);
    }

    public ForestSSE addConsumerMatchesPostfix(String name, String valuePostfix, SSEStringMessageConsumer consumer) {
        return addConsumer(name, eventSource -> eventSource.getValue().endsWith(valuePostfix), consumer);
    }

    public ForestSSE addConsumerMatches(String name, String valueRegex, SSEStringMessageConsumer consumer) {
        return addConsumer(name, eventSource -> eventSource.getValue().matches(valueRegex), consumer);
    }

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


    public ForestSSE setOnOpen(Consumer<EventSource> onOpenConsumer) {
        this.onOpenConsumer = onOpenConsumer;
        return this;
    }

    public ForestSSE setOnClose(BiConsumer<ForestRequest, ForestResponse> onCloseConsumer) {
        this.onCloseConsumer = onCloseConsumer;
        return this;
    }

    public ForestSSE addOnData(SSEStringMessageConsumer consumer) {
        return addConsumer("data", consumer);
    }

    public <T> ForestSSE addOnData(Class<T> valueClass, SSEMessageConsumer<T> consumer) {
        return addConsumer("data", valueClass, consumer);
    }

    public ForestSSE addOnDataMatchesPrefix(String valuePrefix, SSEStringMessageConsumer consumer) {
        return addConsumerMatchesPrefix("data", valuePrefix, consumer);
    }

    public ForestSSE addOnDataMatchesPostfix(String valuePrefix, SSEStringMessageConsumer consumer) {
        return addConsumerMatchesPostfix("data", valuePrefix, consumer);
    }

    public ForestSSE addOnEvent(SSEStringMessageConsumer consumer) {
        return addConsumer("event", consumer);
    }

    public <T> ForestSSE addOnEvent(Class<T> valueClass, SSEMessageConsumer<T> consumer) {
        return addConsumer("event", valueClass, consumer);
    }

    public ForestSSE addOnEventMatchesPrefix(String valuePrefix, SSEStringMessageConsumer consumer) {
        return addConsumerMatchesPrefix("event", valuePrefix, consumer);
    }

    public ForestSSE addOnEventMatchesPostfix(String valuePrefix, SSEStringMessageConsumer consumer) {
        return addConsumerMatchesPostfix("event", valuePrefix, consumer);
    }

    public ForestSSE addOnId(SSEStringMessageConsumer consumer) {
        return addConsumer("id", consumer);
    }

    public <T> ForestSSE addOnId(Class<T> valueClass, SSEMessageConsumer<T> consumer) {
        return addConsumer("id", valueClass, consumer);
    }

    public ForestSSE addOnIdMatchesPrefix(String valuePrefix, SSEStringMessageConsumer consumer) {
        return addConsumerMatchesPrefix("id", valuePrefix, consumer);
    }

    public ForestSSE addOnIdMatchesPostfix(String valuePrefix, SSEStringMessageConsumer consumer) {
        return addConsumerMatchesPostfix("id", valuePrefix, consumer);
    }

    public ForestSSE addOnRetry(SSEStringMessageConsumer consumer) {
        return addConsumer("retry", consumer);
    }

    public <T> ForestSSE addOnRetry(Class<T> valueClass, SSEMessageConsumer<T> consumer) {
        return addConsumer("retry", valueClass, consumer);
    }

    public ForestSSE addOnRetryMatchesPrefix(String valuePrefix, SSEStringMessageConsumer consumer) {
        return addConsumerMatchesPrefix("retry", valuePrefix, consumer);
    }

    public ForestSSE addOnRetryMatchesPostfix(String valuePrefix, SSEStringMessageConsumer consumer) {
        return addConsumerMatchesPostfix("retry", valuePrefix, consumer);
    }

    protected void onOpen(EventSource eventSource) {
        if (onOpenConsumer != null) {
            onOpenConsumer.accept(eventSource);
        }
    }

    protected void onClose(ForestRequest request, ForestResponse response) {
        if (onCloseConsumer != null) {
            onCloseConsumer.accept(request, response);
        }
    }

    @Override
    public void onMessage(EventSource eventSource, String name, String value) {
        final List<SSEStringMessageConsumer> consumers = consumerMap.get(name);
        if (CollectionUtils.isEmpty(consumers)) {
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

    @Override
    public ForestRequest getRequest() {
        return this.request;
    }

    private String parseLine(String line) {
        final String[] group = line.split("\\:", 2);
        if (group.length == 1) {
            return group[0];
        }
        return StringUtils.trimBegin(group[1]);
    }


    private EventSource parseEventSource(ForestResponse response, String line) {
        final String[] group = line.split("\\:", 2);
        if (group.length == 1) {
            return new EventSource("", request, response, line, line);
        }
        final String name = group[0].trim();
        final String data = StringUtils.trimBegin(group[1]);
        return new EventSource(name, request, response, line, data);
    }


    @Override
    public <R extends ForestSSE> R listen() {
        final boolean isAsync = this.request.isAsync();
        final ForestResponse response = isAsync ? this.request.executeAsFuture().await() : this.request.executeAsResponse();
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

    @Override
    public <R extends ForestSSE> CompletableFuture<R> asyncListen() {
        return CompletableFuture.supplyAsync(this::listen);
    }

    @Override
    public <R extends ForestSSE> CompletableFuture<R> asyncListen(ExecutorService pool) {
        return CompletableFuture.supplyAsync(this::listen, pool);
    }
}
