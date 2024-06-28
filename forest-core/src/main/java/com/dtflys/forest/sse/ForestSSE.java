package com.dtflys.forest.sse;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.utils.StringUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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

public class ForestSSE implements ForestSSEListener {

    private final ForestRequest request;

    private Consumer<EventSource> onOpenConsumer;

    private BiConsumer<ForestRequest, ForestResponse> onCloseConsumer;

    private Map<String, List<SSEMessageConsumer>> consumerMap = new ConcurrentHashMap<>();


    public ForestSSE(final ForestRequest request) {
        this.request = request;
    }

    public ForestSSE addConsumer(String name, SSEMessageConsumer consumer) {
        consumerMap.computeIfAbsent(name, k -> new CopyOnWriteArrayList<>()).add(consumer);
        return this;
    }

    public ForestSSE addConsumer(String name, Function<EventSource, Boolean> matcher, SSEMessageConsumer consumer) {
        consumerMap.computeIfAbsent(name, k -> new CopyOnWriteArrayList<>()).add(new SSEMessageConsumer() {
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

    public ForestSSE addConsumerOnMatchesPrefix(String name, String valuePrefix, SSEMessageConsumer consumer) {
        return addConsumer(name, eventSource -> eventSource.getValue().startsWith(valuePrefix), consumer);
    }


    public ForestSSE addConsumerOnMatchesPostfix(String name, String valuePostfix, SSEMessageConsumer consumer) {
        return addConsumer(name, eventSource -> eventSource.getValue().endsWith(valuePostfix), consumer);
    }

    public ForestSSE addConsumerOnMatches(String name, String valueRegex, SSEMessageConsumer consumer) {
        return addConsumer(name, eventSource -> eventSource.getValue().matches(valueRegex), consumer);
    }



    public ForestSSE onOpen(Consumer<EventSource> onOpenConsumer) {
        this.onOpenConsumer = onOpenConsumer;
        return this;
    }

    public ForestSSE onClose(BiConsumer<ForestRequest, ForestResponse> onCloseConsumer) {
        this.onCloseConsumer = onCloseConsumer;
        return this;
    }


    public ForestSSE onData(SSEMessageConsumer consumer) {
        return addConsumer("data", consumer);
    }

    public ForestSSE onEvent(SSEMessageConsumer consumer) {
        return addConsumer("event", consumer);
    }


    public ForestSSE onId(SSEMessageConsumer consumer) {
        return addConsumer("id", consumer);
    }

    public ForestSSE onRetry(SSEMessageConsumer consumer) {
        return addConsumer("retry", consumer);
    }

    private void onOpen(EventSource eventSource) {
        if (onOpenConsumer != null) {
            onOpenConsumer.accept(eventSource);
        }
        onMessage(eventSource, "", "");
    }

    private void onClose(ForestRequest request, ForestResponse response) {
        if (onCloseConsumer != null) {
            onCloseConsumer.accept(request, response);
        }
    }

    @Override
    public void onMessage(EventSource eventSource, String name, String value) {
        final List<SSEMessageConsumer> consumers = consumerMap.get(name);
        if (CollectionUtils.isNotEmpty(consumers)) {
            return;
        }
        for (final SSEMessageConsumer consumer : consumers) {
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
    public ForestResponse listen() {
        final ForestResponse response = this.request.executeAsResponse();
        if (response.isError()) {
            return response;
        }
        final EventSource openEventSource = new EventSource("open", request, response);
        this.onOpen(openEventSource);
        if (SSEMessageResult.CLOSE.equals(openEventSource.getMessageResult())) {
            onClose(request, response);
            return response;
        }
        try {
            final String charset = Optional.ofNullable(response.getCharset()).orElse("UTF-8");
            final InputStream in = response.getInputStream();
            final InputStreamReader isr = new InputStreamReader(in, charset);
            try (BufferedReader reader = new BufferedReader(isr)) {
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
        return response;
    }

    @Override
    public CompletableFuture<ForestResponse> asyncListen() {
        return CompletableFuture.supplyAsync(this::listen);
    }

    @Override
    public CompletableFuture<ForestResponse> asyncListen(ExecutorService pool) {
        return CompletableFuture.supplyAsync(this::listen, pool);
    }
}
