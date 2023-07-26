package org.dromara.forest.http;


import java.lang.ref.WeakReference;

public class RequestProperty<T> {

    private WeakReference<ForestRequest<?>> requestRef;

    private T value;

    private Lazy<T> lazyValue;

    public RequestProperty<T> bindRequest(ForestRequest<?> request) {
        this.requestRef = new WeakReference<>(request);
        return this;
    }

    public RequestProperty<T> set(T value) {
        this.value = value;
        return this;
    }

    public T get() {
        if (value != null) {
            return value;
        }
        if (lazyValue != null && requestRef != null) {
            final ForestRequest<?> req = requestRef.get();
            if (req != null) {
                return lazyValue.eval(req);
            }
        }
        return null;
    }

    public RequestProperty<T> bind(Lazy<T> value) {
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
