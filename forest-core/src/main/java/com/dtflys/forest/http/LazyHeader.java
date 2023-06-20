package com.dtflys.forest.http;

import com.dtflys.forest.exceptions.ForestRuntimeException;


public class LazyHeader implements ForestHeader<LazyHeader, Lazy<?>> {

    private final ForestHeaderMap headerMap;

    private final String name;

    private Lazy<?> lazyValue;

    public LazyHeader(ForestHeaderMap headerMap, String name, Lazy<?> lazyValue) {
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
        final HasURL hasURL = headerMap.getHasURL();
        if (!(hasURL instanceof ForestRequest)) {
            throw new ForestRuntimeException(
                    "the request of header[name=" + name + "] dose not exist");
        }
        final ForestRequest request = (ForestRequest) hasURL;
        if (lazyValue == null) {
            throw new ForestRuntimeException("the lazy value of header[name=" + name + "] is null");
        }
        final Object ret = lazyValue.eval(request);
        if (ret == null) {
            return null;
        }
        return String.valueOf(ret);
    }

    @Override
    public LazyHeader setValue(Lazy<?> lazyValue) {
        this.lazyValue = lazyValue;
        return this;
    }

    @Override
    public LazyHeader clone(ForestHeaderMap headerMap) {
        return new LazyHeader(headerMap, name, lazyValue);
    }
}
