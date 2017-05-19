package org.forest.handler;

import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 16:49
 */
public interface ResponseHandler {

    void handle(ForestRequest request, ForestResponse response);
}
