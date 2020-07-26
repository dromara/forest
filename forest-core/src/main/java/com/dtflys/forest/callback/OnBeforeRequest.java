package com.dtflys.forest.callback;

import com.dtflys.forest.http.ForestRequest;

/**
 * @author gongjun
 * @since 2020-07-26
 */
public interface OnBeforeRequest {

    void onBeforeRequest(ForestRequest request);
}
