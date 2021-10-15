package com.dtflys.forest.mapping;

import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.MetaRequest;
import com.dtflys.forest.utils.StringUtils;
import com.dtflys.forest.utils.URLUtils;

public class MappingUrlEncodedExpr extends MappingExpr {

    private final MappingExpr expr;

    protected MappingUrlEncodedExpr(ForestMethod<?> forestMethod, MappingExpr expr) {
        super(forestMethod, expr.token);
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
    public Object render(Object[] args) {
        Object ret = expr.render(args);
        if (ret == null) {
            return null;
        }
        String str = String.valueOf(ret);
/*
        MetaRequest metaRequest = forestMethod.getMetaRequest();
        String charset = null;
        if (metaRequest != null) {
            charset = metaRequest.getCharset();
        }
        Object encoded = null;
        if (StringUtils.isNotBlank(charset)) {
            encoded = URLUtils.pathEncode(str, charset);
        }
        if (encoded == null) {
            encoded = URLUtils.pathEncode(str, "UTF-8");
        }
        return encoded;
*/
        return str;
    }

    @Override
    public String toString() {
        return "[Encode: " + expr.toString() + "]";
    }
}
