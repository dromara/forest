package org.forest.mapping;

/**
 * Created by Administrator on 2016/5/4.
 */
public class MappingString extends MappingExpr {

    private final String text;

    public String getText() {
        return text;
    }

    public MappingString(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text.toString();
    }
}
