package com.dtflys.forest.solon.test.when;

import com.dtflys.forest.callback.RetryWhen;
import com.dtflys.forest.callback.SuccessWhen;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import org.noear.solon.annotation.Component;

@Component
public class MyWhenCondition implements SuccessWhen, RetryWhen {

    private volatile int successInvokeCount = 0;

    private volatile int retryInvokeCount = 0;

    private int rejectStatusCode = -1;

    private int retryStatusCode = -1;

    @Override
    public boolean successWhen(ForestRequest req, ForestResponse res) {
        successInvokeCount++;
        return res.noException() && res.statusOk() && res.statusIsNot(rejectStatusCode);
    }

    @Override
    public boolean retryWhen(ForestRequest req, ForestResponse res) {
        retryInvokeCount++;
        return res.noException() && res.statusOk() && res.statusIs(retryStatusCode);
    }

    public void setRejectStatusCode(int rejectStatusCode) {
        this.rejectStatusCode = rejectStatusCode;
    }

    public int getSuccessInvokeCount() {
        return successInvokeCount;
    }

    public void setSuccessInvokeCount(int successInvokeCount) {
        this.successInvokeCount = successInvokeCount;
    }

    public void setRetryStatusCode(int retryStatusCode) {
        this.retryStatusCode = retryStatusCode;
    }

    public int getRetryInvokeCount() {
        return retryInvokeCount;
    }

    public void setRetryInvokeCount(int retryInvokeCount) {
        this.retryInvokeCount = retryInvokeCount;
    }
}
