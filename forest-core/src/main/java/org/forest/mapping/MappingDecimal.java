package org.forest.mapping;

import java.math.BigDecimal;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-01-09 17:23
 */
public class MappingDecimal extends MappingExpr {

    private final BigDecimal number;

    public MappingDecimal(BigDecimal number) {
        this.number = number;
    }

    @Override
    public Object render(Object[] args) {
        return number.toString();
    }

    public BigDecimal getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "[Dec: " + number + "]";
    }

}
