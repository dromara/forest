package org.forest.executors;

import org.forest.http.ForestRequest;

import java.io.UnsupportedEncodingException;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 14:50
 */
public interface BodyBuilder<R> {

    void buildBody(R req, ForestRequest request);
}
