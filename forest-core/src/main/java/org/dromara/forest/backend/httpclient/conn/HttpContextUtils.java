package org.dromara.forest.backend.httpclient.conn;

import org.apache.http.protocol.HttpContext;
import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.http.ForestRequest;

public class HttpContextUtils {

    public static ForestRequest<?> getCurrentRequest(HttpContext context) {
        Object request = context.getAttribute("REQUEST");
        if (request == null) {
            throw new ForestRuntimeException("Current Forest request is NULL!");
        }
        return (ForestRequest<?>) request;
    }

}
