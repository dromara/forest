package com.dtflys.forest.mapping;

/**
 * Created by Administrator on 2016/5/4.
 */
public class MappingVariable extends MappingParameter {


    public MappingVariable(String name, Class type) {
        super(type);
        this.name = name;
    }

    public String getName() {
        return name;
    }


    @Override
    public String toString() {
        return "[DataMapping: " + name + "]";
    }
}
