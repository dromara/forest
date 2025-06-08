package com.dtflys.forest.reflection;

public class VariableSubscriber {

    private volatile boolean alwaysChanged = false;

    private volatile boolean changed = false;

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public boolean isAlwaysChanged() {
        return alwaysChanged;
    }

    public void setAlwaysChanged(boolean alwaysChanged) {
        this.alwaysChanged = alwaysChanged;
    }
}
