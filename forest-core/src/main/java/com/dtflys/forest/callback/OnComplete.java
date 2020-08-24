package com.dtflys.forest.callback;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;

/**
 * @author gongjun
 * @since 2020-07-26
 */
public interface OnComplete {

    void onSuccess(ForestRequest request, ForestResponse response);

}
