package com.dtflys.forest.exceptions;

import com.dtflys.forest.http.ForestRequest;

public class ForestRetryException extends ForestRuntimeException {

    private final int maxRetryCount;
    private final int currentRetryCount;
    private final ForestRequest request;
    private final boolean needRetry;

    public ForestRetryException(ForestRequest request, int maxRetryCount, int currentRetryCount, boolean needRetry) {
        super("[Forest] retry count: " + currentRetryCount);
        this.needRetry = needRetry;
        this.request = request;
        this.maxRetryCount = maxRetryCount;
        this.currentRetryCount = currentRetryCount;
    }


    public ForestRetryException(ForestRequest request, int maxRetryCount, int currentRetryCount) {
        this(request, maxRetryCount, currentRetryCount, true);
    }


    public ForestRetryException(Throwable cause, ForestRequest request, int maxRetryCount, int currentRetryCount) {
        super("[Forest] retry count: " + currentRetryCount + ", cause: " + cause.getMessage(), cause);
        this.needRetry = true;
        this.request = request;
        this.maxRetryCount = maxRetryCount;
        this.currentRetryCount = currentRetryCount;
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public int getCurrentRetryCount() {
        return currentRetryCount;
    }

    public ForestRequest getRequest() {
        return request;
    }

    public boolean isNeedRetry() {
        return needRetry;
    }

    /**
     * 是否达到最大请求重试次数
     *
     * @return {@code true}: 已达到, {@code false}: 未达到
     */
    public boolean isMaxRetryCountReached() {
        return currentRetryCount == maxRetryCount;
    }

    @Override
    public synchronized Throwable getCause() {
        Throwable cause = super.getCause();
        if (cause != null) {
            return cause;
        }
        return new ForestRetryException(request, maxRetryCount, currentRetryCount, false);
    }
}
