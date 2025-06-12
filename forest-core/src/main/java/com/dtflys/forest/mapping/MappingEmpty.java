package com.dtflys.forest.mapping;

public class MappingEmpty {

    public final static MappingEmpty OPTIONAL = new MappingEmpty();

    private MappingEmpty() {}

    @Override
    public String toString() {
        return "null";
    }
}
