package com.dtflys.test.http;

import com.dtflys.forest.backend.httpclient.response.HttpclientForestResponseFactory;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.reflection.NoneLifeCycleHandler;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.junit.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-12 17:58
 */
public class TestResponseFactory {

    @Test
    public void  testHttpclientForestResponseFactory() {
        ForestRequest request = new ForestRequest(ForestConfiguration.configuration());
        Date requestTime = new Date();
        HttpclientForestResponseFactory responseFactory = new HttpclientForestResponseFactory();
        HttpResponse httpResponse = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);
        when(statusLine.getStatusCode()).thenReturn(200);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        LifeCycleHandler lifeCycleHandler = new NoneLifeCycleHandler();
        ForestResponse response = responseFactory.createResponse(request, httpResponse, lifeCycleHandler, null, requestTime);
        assertThat(response)
                .isNotNull()
                .extracting(
                        ForestResponse::getStatusCode,
                        ForestResponse::getContent,
                        ForestResponse::getResult)
                .contains(200, "", null);
    }

}
