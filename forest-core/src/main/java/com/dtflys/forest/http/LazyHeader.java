package com.dtflys.forest.http;

import com.dtflys.forest.callback.Lazy;
import com.dtflys.forest.exceptions.ForestRuntimeException;

import java.lang.ref.WeakReference;
import java.util.function.Function;

public class LazyHeader implements ForestHeader<LazyHeader, Lazy<Object>> {

    private final ForestHeaderMap headerMap;

    private final String name;

    private Lazy<Object> lazyValue;

    public LazyHeader(ForestHeaderMap headerMap, String name, Lazy<Object> lazyValue) {
        this.headerMap = headerMap;
        this.name = name;
        this.lazyValue = lazyValue;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        HasURL hasURL = headerMap.getHasURL();
        if (!(hasURL instanceof ForestRequest)) {
            throw new ForestRuntimeException(
                    "the request of header[name=" + name + "] dose not exist");
        }
        ForestRequest request = (ForestRequest) hasURL;
        if (lazyValue == null) {
            throw new ForestRuntimeException("the lazy value of header[name=" + name + "] is null");
        }
        Object ret = lazyValue.getValue(request);
        if (ret == null) {
            return null;
        }
        return String.valueOf(ret);
    }

    @Override
    public LazyHeader setValue(Lazy<Object> lazyValue) {
        this.lazyValue = lazyValue;
        return this;
    }

    @Override
    public LazyHeader clone(ForestHeaderMap headerMap) {
        return new LazyHeader(headerMap, name, lazyValue);
    }
}
