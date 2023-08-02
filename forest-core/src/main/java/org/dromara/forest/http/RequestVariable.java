package org.dromara.forest.http;



import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class RequestVariable<T> {

    private WeakReference<ForestRequest<?>> requestRef;

    private T value;

    private Lazy<T> lazyValue;

    private final List<RequestVariableListener<T>> listeners = new LinkedList<>();

    public RequestVariable<T> addListener(RequestVariableListener<T> listener) {
        this.listeners.add(listener);
        return this;
    }

    public RequestVariable<T> bindRequest(ForestRequest<?> request) {
        this.requestRef = new WeakReference<>(request);
        return this;
    }

    private void triggerListener(T oldValue, T newValue) {
        if (!listeners.isEmpty() && !Objects.equals(oldValue, value)) {
            for (RequestVariableListener<T> listener : listeners) {
                listener.onRequestVariableValueChanged(this, oldValue, newValue);
            }
        }
    }

    RequestVariable<T> setValue(T value, boolean triggerListener) {
        final T oldValue = getValue(false);
        this.value = value;
        if (triggerListener) {
            triggerListener(oldValue, value);
        }
        return this;
    }

    public RequestVariable<T> set(T value) {
        return setValue(value, true);
    }

    public T get() {
        return getValue(true);
    }

    T getValue(boolean triggerListener) {
        if (value != null) {
            return value;
        }
        if (lazyValue != null && requestRef != null) {
            final ForestRequest<?> req = requestRef.get();
            if (req != null) {
                T newValue = lazyValue.eval(req);
                if (triggerListener) {
                    T oldValue = this.value;
                    triggerListener(oldValue, value);
                }
                return newValue;
            }
        }
        return null;
    }

    public RequestVariable<T> bind(Lazy<T> value) {
        this.lazyValue = value;
        this.value = null;
        return this;
    }

    public boolean isNull() {
        return get() == null;
    }

    @Override
    public String toString() {
        return String.valueOf(get());
    }
}
