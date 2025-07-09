package com.dtflys.forest.mapping;

public interface MappingListener {

    void clear();

    void onChanged(MappingTemplate template, Object newValue);

}
