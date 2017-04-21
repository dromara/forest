package org.forest.handler;

import org.forest.reflection.ForestMethod;
import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;

import java.lang.reflect.Type;

/**
 * Created by Administrator on 2016/5/4.
 */
public abstract class ResponseHandler {

    protected final ForestMethod method;

    public ResponseHandler(ForestMethod method) {
        this.method = method;
    }


    public abstract Object getResult(ForestRequest request, ForestResponse response, Type resultType);
}
