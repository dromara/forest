package com.dtflys.forest.logging;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-09-14 17:31
 */
public interface LogHandler {

    void logRequest(RequestLogMessage requestLogMessage);

    void logResponse(ResponseLogMessage responseLogMessage);

}
