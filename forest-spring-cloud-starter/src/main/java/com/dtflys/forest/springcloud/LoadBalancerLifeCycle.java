package com.dtflys.forest.springcloud;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestURL;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.reflection.ForestMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author changjin wei(魏昌进)
 * @since 1.5.33
 */
public class LoadBalancerLifeCycle implements MethodAnnotationLifeCycle<LoadBalancer, Object> {

    private static final Logger LOG = LoggerFactory.getLogger(LoadBalancerLifeCycle.class);
    private static final String PARAM_KEY_SERVICE_ID = "__service_id";
    private final LoadBalancerClientFactory loadBalancerClientFactory;
    private final LoadBalancerClient loadBalancerClient;

    public LoadBalancerLifeCycle(LoadBalancerClientFactory loadBalancerClientFactory, LoadBalancerClient loadBalancerClient) {
        this.loadBalancerClientFactory = loadBalancerClientFactory;
        this.loadBalancerClient = loadBalancerClient;
    }

    @Override
    public void onMethodInitialized(ForestMethod method, LoadBalancer annotation) {
        final String serviceId = annotation.value();
        method.setExtensionParameterValue(PARAM_KEY_SERVICE_ID, serviceId);
    }


    @Override
    public boolean beforeExecute(ForestRequest request) {
        final URI originalUri = request.url().toURI();
        final String serviceId = (String) request.getMethod().getExtensionParameterValue(PARAM_KEY_SERVICE_ID);
        String hint = getHint(serviceId);
        DefaultRequest<RequestDataContext> lbRequest = new DefaultRequest<>(new RequestDataContext(buildRequestData(request), hint));
        ServiceInstance instance = loadBalancerClient.choose(serviceId, lbRequest);
        if (instance == null) {
            String message = "Load balancer does not contain an instance for the service " + serviceId;
            if (LOG.isWarnEnabled()) {
                LOG.warn(message);
            }
            return true;
        }
        String reconstructedUrl = loadBalancerClient.reconstructURI(instance, originalUri).toString();
         buildRequest(request, reconstructedUrl);
        return true;
    }

    protected void buildRequest(ForestRequest request, String reconstructedUrl) {
        ForestURL forestUrl = request.url();
        URI reconstructedUri = URI.create(reconstructedUrl);
        forestUrl.setScheme(reconstructedUri.getScheme());
        forestUrl.setHost(reconstructedUri.getHost());
        forestUrl.setPort(reconstructedUri.getPort());
    }

    protected RequestData buildRequestData(ForestRequest request) {
        HttpHeaders requestHeaders = new HttpHeaders();
        request.headers().forEach((key, value) -> requestHeaders.put(key, Collections.singletonList(value)));
        return new RequestData(HttpMethod.valueOf(request.getType().name()), request.url().toURI(), requestHeaders, null, new HashMap<>());
    }


    protected String getHint(String serviceId) {
        LoadBalancerProperties properties = loadBalancerClientFactory.getProperties(serviceId);
        String defaultHint = properties.getHint().getOrDefault("default", "default");
        String hintPropertyValue = properties.getHint().get(serviceId);
        return hintPropertyValue != null ? hintPropertyValue : defaultHint;
    }


}
