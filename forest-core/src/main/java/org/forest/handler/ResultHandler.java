package org.forest.handler;

import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;

import java.lang.reflect.Type;

/**
 * Created by Administrator on 2016/5/4.
 */
public abstract class ResultHandler {


    public abstract Object getResult(ForestRequest request, ForestResponse response, Type resultType);
}
