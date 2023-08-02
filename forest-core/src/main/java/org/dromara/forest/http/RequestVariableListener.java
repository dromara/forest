package org.dromara.forest.http;

public interface RequestVariableListener<T> {

    void onRequestVariableValueChanged(RequestVariable<T> variable, T oldValue, T newValue);
}
