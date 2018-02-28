package org.forest.backend;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-27 17:17
 */
public class HttpConnectionConstants {

    /**
     * maximum number of conntections allowed
     */
    public final static int DEFAULT_MAX_TOTAL_CONNECTIONS = 500;
    /**
     * timeout in milliseconds used when retrieving
     */
    public final static int DEFAULT_TIMEOUT = 60000;
    /**
     * connect timeout
     */
    public final static int DEFAULT_CONNECT_TIMEOUT = 10000;
    /**
     * read timeout
     */
    public final static int DEFAULT_READ_TIMEOUT = 10000;

}
