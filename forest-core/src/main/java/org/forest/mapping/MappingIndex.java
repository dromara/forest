package org.forest.mapping;

/**
 * Created by Administrator on 2016/5/17.
 */
public class MappingIndex extends MappingExpr {

    private final Integer index;

    public MappingIndex(Integer index) {
        super(Token.INDEX);
        this.index = index;
    }

    @Override
    public Object render(Object[] args) {
        return args[index];
    }

    public Integer getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "[Index: " + index + "]";
    }
}
