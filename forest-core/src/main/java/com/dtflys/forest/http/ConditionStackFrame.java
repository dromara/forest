package com.dtflys.forest.http;

/**
 * Forest 请求对象条件栈帧
 */
public class ConditionStackFrame {

    private boolean endIf = false;

    private Boolean condition;

    public Boolean getCondition() {
        return condition;
    }

    public ConditionStackFrame setCondition(Boolean condition) {
        this.condition = condition;
        return this;
    }

    public boolean isEndIf() {
        return endIf;
    }

    public ConditionStackFrame setEndIf(boolean endIf) {
        this.endIf = endIf;
        return this;
    }
}
