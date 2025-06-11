package com.dtflys.forest.mapping;

public class MappingEmpty {

    public final static MappingEmpty EMPTY = new MappingEmpty();

    private MappingEmpty() {}

    @Override
    public String toString() {
        return "null";
    }
}
