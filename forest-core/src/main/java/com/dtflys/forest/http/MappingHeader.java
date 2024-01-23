package com.dtflys.forest.http;


import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.mapping.MappingTemplate;

public class MappingHeader implements ForestHeader<MappingHeader, MappingTemplate> {

    private final ForestHeaderMap headerMap;

    /**
     * 请求头名称
     */
    private final String name;

    /**
     * 请求头的值
     */
    private MappingTemplate value;

    public MappingHeader(ForestHeaderMap headerMap, String name, MappingTemplate value) {
        this.headerMap = headerMap;
        this.name = name;
        this.value = value;
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
        return value.render(request);
    }

    @Override
    public MappingHeader setValue(MappingTemplate template) {
        this.value = template;
        return this;
    }

    @Override
    public MappingHeader clone(ForestHeaderMap headerMap) {
        return new MappingHeader(headerMap, this.name, this.value);
    }
}
