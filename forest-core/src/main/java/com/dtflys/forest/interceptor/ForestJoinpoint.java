package com.dtflys.forest.interceptor;

/**
 * Forest 拦截器插入点
 *
 * @since 2.0.0_BETA
 */
public class ForestJoinpoint {

    public final static ForestJoinpoint PROCEED = new ForestJoinpoint(State.PROCEED);

    public final static ForestJoinpoint CUTOFF = new ForestJoinpoint(State.CUTOFF);

    public enum State {
        PROCEED,
        CUTOFF,
        FAIL
    }

    private final State state;

    private final Object result;

    public ForestJoinpoint(State state) {
        this(state, null);
    }


    public ForestJoinpoint(State state, Object result) {
        this.state = state;
        this.result = result;
    }

    public State getState() {
        return state;
    }

    public Object getResult() {
        return result;
    }

    public boolean isProceed() {
        return state == State.PROCEED;
    }

    public boolean isCutoff() {
        return state == State.CUTOFF;
    }
}
