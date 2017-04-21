package org.forest.executors.httpclient;

import org.forest.config.ForestConfiguration;
import org.forest.exceptions.ForestRuntimeException;
import org.forest.executors.HttpExecutor;
import org.forest.executors.ForestExecutorFactory;
import org.forest.http.ForestRequest;
import org.forest.reflection.ForestMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-20 18:23
 */
public class HttpclientExecutorFactory implements ForestExecutorFactory {

    private final static Map<String, HttpExecutorCreator> executorCreatorMap = new HashMap<>();

    static {
        executorCreatorMap.put("GET", new HttpExecutorCreator() {
            @Override
            public HttpExecutor createExecutor(HttpclientConnectionManager connectionManager, ForestRequest request) {
                return new HttpclientGetExecutor(connectionManager, request);
            }
        });

        executorCreatorMap.put("HEAD", new HttpExecutorCreator() {
            @Override
            public HttpExecutor createExecutor(HttpclientConnectionManager connectionManager, ForestRequest request) {
                return new HttpclientHeadExecutor(connectionManager, request);
            }
        });

        executorCreatorMap.put("DELETE", new HttpExecutorCreator() {
            @Override
            public HttpExecutor createExecutor(HttpclientConnectionManager connectionManager, ForestRequest request) {
                return new HttpclientDeleteExecutor(connectionManager, request);
            }
        });


        executorCreatorMap.put("OPTIONS", new HttpExecutorCreator() {
            @Override
            public HttpExecutor createExecutor(HttpclientConnectionManager connectionManager, ForestRequest request) {
                return new HttpclientOptionsExecutor(connectionManager, request);
            }
        });

        executorCreatorMap.put("TRACE", new HttpExecutorCreator() {
            @Override
            public HttpExecutor createExecutor(HttpclientConnectionManager connectionManager, ForestRequest request) {
                return new HttpclientTraceExecutor(connectionManager, request);
            }
        });

        executorCreatorMap.put("POST", new HttpExecutorCreator() {
            @Override
            public HttpExecutor createExecutor(HttpclientConnectionManager connectionManager, ForestRequest request) {
                return new HttpclientPostExecutor(connectionManager, request);
            }
        });

        executorCreatorMap.put("PUT", new HttpExecutorCreator() {
            @Override
            public HttpExecutor createExecutor(HttpclientConnectionManager connectionManager, ForestRequest request) {
                return new HttpclientPutHttpExecutor(connectionManager, request);
            }
        });

    }

    private final ForestConfiguration configuration;

    private final HttpclientConnectionManager connectionManager;

    public HttpclientExecutorFactory(ForestConfiguration configuration) {
        this.configuration = configuration;
        this.connectionManager = new HttpclientConnectionManager(configuration);
    }


    @Override
    public HttpExecutor create(ForestRequest request, ForestMethod method) {
        HttpExecutor executor  = null;
        String key = request.getType().toUpperCase();
        HttpExecutorCreator httpExecutorCreator = executorCreatorMap.get(key);
        if (httpExecutorCreator == null) {
            throw new ForestRuntimeException("http type\"" + key + "\" is not be supported.");
        }
        executor = httpExecutorCreator.createExecutor(connectionManager, request);
        return executor;

    }

    private interface HttpExecutorCreator {
        HttpExecutor createExecutor(HttpclientConnectionManager connectionManager, ForestRequest request);
    }
}
