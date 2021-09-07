package com.dtflys.forest.callback;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.2
 */
public class DefaultRetryWhen implements RetryWhen {

    @Override
    public boolean retryWhen(ForestRequest request, ForestResponse response) {
        return response.isError();
    }
}
