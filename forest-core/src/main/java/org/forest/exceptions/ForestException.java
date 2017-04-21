package org.forest.exceptions;

/**
 * Created by Administrator on 2016/5/4.
 */
public class ForestException extends Exception {

    public ForestException(Throwable cause) {
        super(cause);
    }

    public ForestException(String msg) {
        super(msg);
    }
}
