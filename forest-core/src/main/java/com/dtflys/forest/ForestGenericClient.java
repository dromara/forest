package com.dtflys.forest;

import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;

public interface ForestGenericClient {

    @Request("http://localhost/")
    ForestRequest<?> request();

    @Request("http://localhost/")
    <T> ForestRequest<T> request(Class<T> clazz);
}
