package com.dtflys.forest.mapping;

import org.jetbrains.annotations.NotNull;

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
    
    public static class RenderedValue extends MappingValue implements CharSequence {
        
        String value;
        
        public RenderedValue(Object value) {
            this.value = String.valueOf(value);
        }

        @Override
        public int length() {
            return value.length();
        }

        @Override
        public char charAt(int index) {
            return value.charAt(index);
        }

        @NotNull
        @Override
        public CharSequence subSequence(int start, int end) {
            return value.subSequence(start, end);
        }

        @Override
        public String toString() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            if (value == null) {
                if (obj == EMPTY || obj == null) {
                    return true;
                }
                if (obj instanceof RenderedValue && ((RenderedValue) obj).value == null) {
                    return true;
                }
                return false;
            }
            if (obj instanceof RenderedValue) {
                return value.equals(((RenderedValue) obj).value);
            }
            return value.equals(obj);
        }
    }
}
