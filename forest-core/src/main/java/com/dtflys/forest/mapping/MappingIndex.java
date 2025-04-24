package com.dtflys.forest.mapping;

import com.dtflys.forest.exceptions.ForestIndexReferenceException;
import com.dtflys.forest.http.ForestRequest;

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
    public Object render(ForestRequest request, Object[] args) {
        try {
            if (index < 0) {
                int argIndex = args.length + index;
                return args[argIndex];
            }
            return args[index];
        } catch (Throwable t) {
            throw new ForestIndexReferenceException(index, args.length, startIndex, endIndex);
        }
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
