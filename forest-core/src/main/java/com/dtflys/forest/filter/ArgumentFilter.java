package com.dtflys.forest.filter;

public abstract class ArgumentFilter implements Filter {

    private Object[] args;

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
