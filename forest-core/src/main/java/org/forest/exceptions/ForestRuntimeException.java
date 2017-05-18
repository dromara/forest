package org.forest.exceptions;

/**
 * Created by Administrator on 2016/5/30.
 */
public class ForestRuntimeException extends RuntimeException {

    public ForestRuntimeException(String message) {
        super(message);
    }

    public ForestRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }


    public ForestRuntimeException(Throwable cause) {
        super(cause);
    }


}
