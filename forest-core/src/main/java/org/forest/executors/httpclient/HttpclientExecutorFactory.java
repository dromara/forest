package org.forest.executors.httpclient;

import org.forest.config.ForestConfiguration;
import org.forest.exceptions.ForestRuntimeException;
import org.forest.executors.HttpExecutor;
import org.forest.executors.ForestExecutorFactory;
import org.forest.executors.httpclient.response.AsyncHttpclientResponseHandler;
import org.forest.executors.httpclient.response.HttpclientResponseHandler;
import org.forest.executors.httpclient.response.SyncHttpclientResponseHandler;
import org.forest.handler.ResponseHandler;
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
            public HttpExecutor createExecutor(HttpclientConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler) {
                return new HttpclientGetExecutor(connectionManager, request,
                        getHttpclientResponseHandler(request, responseHandler));
            }
        });

        executorCreatorMap.put("HEAD", new HttpExecutorCreator() {
            @Override
            public HttpExecutor createExecutor(HttpclientConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler) {
                return new HttpclientHeadExecutor(connectionManager, request,
                        getHttpclientResponseHandler(request, responseHandler));
            }
        });

        executorCreatorMap.put("DELETE", new HttpExecutorCreator() {
            @Override
            public HttpExecutor createExecutor(HttpclientConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler) {
                return new HttpclientDeleteExecutor(connectionManager, request,
                        getHttpclientResponseHandler(request, responseHandler));
            }
        });


        executorCreatorMap.put("OPTIONS", new HttpExecutorCreator() {
            @Override
            public HttpExecutor createExecutor(HttpclientConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler) {
                return new HttpclientOptionsExecutor(connectionManager, request,
                        getHttpclientResponseHandler(request, responseHandler));
            }
        });

        executorCreatorMap.put("TRACE", new HttpExecutorCreator() {
            @Override
            public HttpExecutor createExecutor(HttpclientConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler) {
                return new HttpclientTraceExecutor(connectionManager, request,
                        getHttpclientResponseHandler(request, responseHandler));
            }
        });

        executorCreatorMap.put("POST", new HttpExecutorCreator() {
            @Override
            public HttpExecutor createExecutor(HttpclientConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler) {
                return new HttpclientPostExecutorHttpclient(connectionManager, request,
                        getHttpclientResponseHandler(request, responseHandler));
            }
        });

        executorCreatorMap.put("PUT", new HttpExecutorCreator() {
            @Override
            public HttpExecutor createExecutor(HttpclientConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler) {
                return new HttpclientPutExecutorHttpclient(connectionManager, request,
                        getHttpclientResponseHandler(request, responseHandler));
            }
        });

        executorCreatorMap.put("PATCH", new HttpExecutorCreator() {
            @Override
            public HttpExecutor createExecutor(HttpclientConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler) {
                return new HttpclientPatchExecutorHttpclient(connectionManager, request,
                        getHttpclientResponseHandler(request, responseHandler));
            }
        });
    }


    private static HttpclientResponseHandler getHttpclientResponseHandler(ForestRequest request, ResponseHandler responseHandler) {
        if (request.isAsync()) {
            return new AsyncHttpclientResponseHandler(request, responseHandler);
        }
        return new SyncHttpclientResponseHandler(request, responseHandler);
    }

    private final ForestConfiguration configuration;

    private final HttpclientConnectionManager connectionManager;

    public HttpclientExecutorFactory(ForestConfiguration configuration) {
        this.configuration = configuration;
        this.connectionManager = new HttpclientConnectionManager(configuration);
    }


    @Override
    public HttpExecutor create(ForestRequest request, ResponseHandler responseHandler) {
        String key = request.getType().toUpperCase();
        HttpExecutorCreator httpExecutorCreator = executorCreatorMap.get(key);
        if (httpExecutorCreator == null) {
            throw new ForestRuntimeException("Http request type\"" + key + "\" is not be supported.");
        }
        HttpExecutor executor = httpExecutorCreator.createExecutor(connectionManager, request, responseHandler);
        return executor;
    }

    private interface HttpExecutorCreator {
        HttpExecutor createExecutor(HttpclientConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler);
    }
}
