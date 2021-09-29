package com.dtflys.forest.exceptions;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-08-02 19:40
 */
public class ForestUnsupportException extends ForestRuntimeException {

    private final String unsupported;

    public ForestUnsupportException(String unsupported) {
        super("[Forest] '" + unsupported + "' is unsupported");
        this.unsupported = unsupported;
    }

    public String getUnsupported() {
        return unsupported;
    }
}
