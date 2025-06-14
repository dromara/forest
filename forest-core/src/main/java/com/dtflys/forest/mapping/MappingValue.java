package com.dtflys.forest.mapping;

public class MappingValue {

    public final static MappingValue EMPTY = new MappingValue();

    private MappingValue() {}

    @Override
    public String toString() {
        return "null";
    }
    
    
    public static MappingValue.RenderedValue rendered(Object value) {
        if (value == null || value == EMPTY || value instanceof RenderedValue) {
            return null;
        }
        return new MappingValue.RenderedValue(value);
    }
    
    public static class RenderedValue extends MappingValue {
        
        Object value;
        
        public RenderedValue(Object value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }
}
