package org.forest.backend.okhttp3.body;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-27 18:52
 */
public class OkHttp3PatchBodyBuilder extends AbstractOkHttp3BodyBuilder {

    @Override
    protected void setBody(Request.Builder builder, RequestBody body) {
        builder.patch(body);
    }
}
