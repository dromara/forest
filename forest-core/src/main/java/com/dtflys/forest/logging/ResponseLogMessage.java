package com.dtflys.forest.logging;

import com.dtflys.forest.http.ForestResponse;

public class ResponseLogMessage {

    private ForestResponse response;

    private long time;

    private String status;

    public ForestResponse getResponse() {
        return response;
    }

    public void setResponse(ForestResponse response) {
        this.response = response;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
