package com.dtflys.forest.utils;

import com.dtflys.forest.http.ForestRequest;

public class ForestProgress {

    private final ForestRequest request;

    private final long currentBytes;

    private final long totalBytes;

    private final boolean isDone;

    public ForestProgress(ForestRequest request, long currentBytes, long totalBytes, boolean isDone) {
        this.request = request;
        this.currentBytes = currentBytes;
        this.totalBytes = totalBytes;
        this.isDone = isDone;
    }

    public ForestRequest getRequest() {
        return request;
    }


    public long getCurrentBytes() {
        return currentBytes;
    }


    public long getTotalBytes() {
        return totalBytes;
    }

    public double getRate() {
        return currentBytes * 1.0F / totalBytes;
    }

    public boolean isDone() {
        return isDone;
    }

}
