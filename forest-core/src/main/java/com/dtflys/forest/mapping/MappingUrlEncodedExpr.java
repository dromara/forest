package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.reflection.ForestMethod;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MappingUrlEncodedExpr extends MappingExpr {

    private final MappingExpr expr;

    protected MappingUrlEncodedExpr(MappingTemplate source, MappingExpr expr) {
        super(source, expr.token);
        this.expr = expr;
    }


    @Override
    public boolean isIterateVariable() {
        return expr.isIterateVariable();
    }

    public MappingExpr getExpr() {
        return expr;
    }

    @Override
    public Object render(VariableScope scope, Object[] args) {
        final Object ret = expr.render(scope, args);
        if (ret == null) {
            return null;
        }
        if (ret instanceof MappingValue) {
            return ret;
        }
        if (ret instanceof Collection) {
            final List<String> list = new ArrayList<>();
            for (Object o : (Collection) ret) {
                list.add(String.valueOf(o));
            }
            return list;
        }
        if (ret.getClass().isArray()) {
            final List<String> list = new ArrayList<>();
            for (int i = 0; i < Array.getLength(ret); i++) {
                list.add(String.valueOf(Array.get(ret, i)));
            }
            return list;
        }
        return String.valueOf(ret);
    }

    @Override
    public String toString() {
        return "[Encode: " + expr.toString() + "]";
    }
}
