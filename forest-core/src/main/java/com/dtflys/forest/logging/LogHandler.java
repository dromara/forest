package com.dtflys.forest.logging;

public interface LogHandler {

    void logRequest(RequestLogMessage requestLogMessage);

    void logResponse(ResponseLogMessage responseLogMessage);

}
