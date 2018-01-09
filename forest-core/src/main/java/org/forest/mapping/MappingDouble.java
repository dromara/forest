package org.forest.mapping;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-01-09 17:44
 */
public class MappingDouble extends MappingExpr {

    private final double number;

    public MappingDouble(double number) {
        this.number = number;
    }

    @Override
    public Object render(Object[] args) {
        return number;
    }

    public double getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "[Double: " + number + "]";
    }

}
