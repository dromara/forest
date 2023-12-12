package com.dtflys.forest.interceptor;

/**
 * Forest 拦截器切入点
 *
 * @since 2.0.0_BETA
 */
public class ForestJointPoint {

    public final static ForestJointPoint PROCEED = new ForestJointPoint(State.PROCEED);

    public final static ForestJointPoint CUTOFF = new ForestJointPoint(State.CUTOFF);

    public enum State {
        PROCEED,
        CUTOFF,
        FAIL
    }

    private final State state;

    private final Object result;

    public ForestJointPoint(State state) {
        this(state, null);
    }


    public ForestJointPoint(State state, Object result) {
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
