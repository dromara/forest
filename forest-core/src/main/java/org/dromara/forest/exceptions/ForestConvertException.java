package org.dromara.forest.exceptions;

import org.dromara.forest.converter.ForestConverter;
import org.joda.time.convert.Converter;

public class ForestConvertException extends ForestRuntimeException {

    private final Class<? extends Converter> converterClass;

    public ForestConvertException(ForestConverter<?> converter, Throwable th) {
        super("[Forest] " + converter.getDataType().getName() +
                " converter: '" + converter.getClass().getSimpleName() +
                "' error: " + errorMessage(th), th);
        this.converterClass = (Class<? extends Converter>) converter.getClass();
    }

    private static String errorMessage(Throwable th) {
        if (th.getMessage() != null) {
            return th.getMessage();
        }
        if (th.getCause() == null || th.getClass().equals(th.getCause().getClass())) {
            return "";
        }
        return errorMessage(th.getCause());
    }

    public Class<? extends Converter> getConverterClass() {
        return converterClass;
    }
}
