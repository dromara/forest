package com.dtflys.forest.mapping;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.reflection.ForestMethod;

/**
 * Created by Administrator on 2016/5/17.
 */
public class MappingIndex extends MappingExpr {

    private final Integer index;

    public MappingIndex(Integer index, int startIndex, int endIndex) {
        super(null, Token.INDEX);
        this.index = index;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    public Object render(Object[] args) {
        if (index < 0) {
            int argIndex = args.length + index;
            if (argIndex < 0) {
                throw new ForestRuntimeException(new IndexOutOfBoundsException(index + " is a invalid index"));
            }
            return args[argIndex];
        }
        return args[index];
    }

    @Override
    public boolean isIterateVariable() {
        return false;
    }

    public Integer getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "[Index: " + index + "]";
    }
}
