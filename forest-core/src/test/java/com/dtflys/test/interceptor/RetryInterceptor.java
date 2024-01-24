package com.dtflys.test.interceptor;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;

public class RetryInterceptor implements Interceptor {


    @Override
    public void afterExecute(ForestRequest request, ForestResponse response) {
        Integer count = getAttributeAsInteger(request, "retry.count");
        request.attachment("count", 1);
        if (count == null) {
            count = 0;
        }
        if (count >= 5) {
            return;
        }
        count++;

    }
}
