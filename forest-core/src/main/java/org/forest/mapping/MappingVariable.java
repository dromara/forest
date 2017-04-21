package org.forest.mapping;

/**
 * Created by Administrator on 2016/5/4.
 */
public class MappingVariable extends MappingParameter {

    private Class type;

    public MappingVariable(String name, Class type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Class getType() {
        return type;
    }

    @Override
    public String toString() {
        return "[DataMapping: " + name + "]";
    }
}
