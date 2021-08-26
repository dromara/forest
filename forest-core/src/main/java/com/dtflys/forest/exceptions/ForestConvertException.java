package com.dtflys.forest.exceptions;

import com.dtflys.forest.converter.ForestConverter;
import org.joda.time.convert.Converter;

public class ForestConvertException extends ForestRuntimeException {

    private final Class<? extends Converter> converterClass;

    public ForestConvertException(ForestConverter<?> converter, Throwable th) {
        super("[Forest] " + converter.getDataType().getName() +
                " converter: '" + converter.getClass().getSimpleName() +
                "' error: " + th.getMessage(), th);
        this.converterClass = (Class<? extends Converter>) converter.getClass();
    }

    public Class<? extends Converter> getConverterClass() {
        return converterClass;
    }
}
