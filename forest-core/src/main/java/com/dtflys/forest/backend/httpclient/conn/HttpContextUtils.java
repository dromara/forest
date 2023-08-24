package com.dtflys.forest.backend.httpclient.conn;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import org.apache.http.protocol.HttpContext;

public class HttpContextUtils {

    public static ForestRequest<?> getCurrentRequest(HttpContext context) {
        Object request = context.getAttribute("REQUEST");
        if (request == null) {
            throw new ForestRuntimeException("Current Forest request is NULL!");
        }
        return (ForestRequest<?>) request;
    }

}
