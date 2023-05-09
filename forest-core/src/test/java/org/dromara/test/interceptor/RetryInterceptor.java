package org.dromara.test.interceptor;

import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;
import org.dromara.forest.interceptor.Interceptor;

public class RetryInterceptor implements Interceptor {


    @Override
    public void afterExecute(ForestRequest request, ForestResponse response) {
        Integer count = getAttributeAsInteger(request, "retry.count");
        request.addAttachment("count", 1);
        if (count == null) {
            count = 0;
        }
        if (count >= 5) {
            return;
        }
        count++;

    }
}
