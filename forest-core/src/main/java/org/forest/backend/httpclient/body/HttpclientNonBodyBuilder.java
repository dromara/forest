package org.forest.backend.httpclient.body;

import org.forest.backend.BodyBuilder;
import org.forest.http.ForestRequest;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 15:11
 */
public class HttpclientNonBodyBuilder implements BodyBuilder {
    @Override
    public void buildBody(Object req, ForestRequest request) {
    }
}
