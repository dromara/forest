package org.forest.http;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-12 17:05
 */
public interface ForestResponseFactory<R> {

    ForestResponse createResponse(ForestRequest request, R res);

}
