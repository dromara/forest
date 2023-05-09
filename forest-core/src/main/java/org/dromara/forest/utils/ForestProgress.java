package org.dromara.forest.utils;

import org.dromara.forest.http.ForestRequest;

public class ForestProgress {

    private final ForestRequest request;

    private long currentBytes;

    private final long totalBytes;

    private boolean isBegin;

    private boolean isDone;

    public ForestProgress(ForestRequest request, long totalBytes) {
        this.request = request;
        this.totalBytes = totalBytes;
    }

    public ForestRequest getRequest() {
        return request;
    }

    public void setCurrentBytes(long currentBytes) {
        this.currentBytes = currentBytes;
    }

    public long getCurrentBytes() {
        return currentBytes;
    }


    public long getTotalBytes() {
        return totalBytes;
    }

    public double getRate() {
        if (totalBytes < 0) {
            return 0;
        }
        return currentBytes * 1.0F / totalBytes;
    }

    public boolean isBegin() {
        return isBegin;
    }

    public void setBegin(boolean begin) {
        isBegin = begin;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public boolean isDone() {
        return isDone;
    }

}
