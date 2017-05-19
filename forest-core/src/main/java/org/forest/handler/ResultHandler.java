package org.forest.handler;

import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;

import java.lang.reflect.Type;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2016-05-04
 */
public abstract class ResultHandler {

    public abstract Object getResult(ForestRequest request, ForestResponse response, Type resultType, Class resultClass);
}
