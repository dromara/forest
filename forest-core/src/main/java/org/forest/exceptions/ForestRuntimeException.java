package org.forest.exceptions;

import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;

/**
 * Created by Administrator on 2016/5/30.
 */
public class ForestRuntimeException extends RuntimeException {


    public ForestRuntimeException(ForestNetworkException cause) {
        super(cause);
    }

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
