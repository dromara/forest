package com.dtflys.forest.result;

import com.dtflys.forest.handler.ResultHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;

import java.lang.reflect.Type;

public abstract class AfterExecuteResultTypeHandler<T> extends ResultTypeHandler<T> {

    public AfterExecuteResultTypeHandler(Class<T> resultClass) {
        super(ReturnFlag.AFTER_EXECUTE, resultClass);
    }

    public boolean matchType(Type resultType, Class resultClass) {
        return super.matchType(resultClass);
    }

    public abstract Object getResult(ResultHandler resultHandler, ForestRequest request, ForestResponse response, Type resultType, Class resultClass);

}
