package com.dtflys.forest;

import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.http.ForestRequest;

public interface ForestGenericClient {

    @Request("/")
    <T> ForestRequest<T> request();
}
