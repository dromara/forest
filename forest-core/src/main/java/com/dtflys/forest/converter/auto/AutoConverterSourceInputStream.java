package com.dtflys.forest.converter.auto;

import java.io.IOException;
import java.io.InputStream;

public class AutoConverterSourceInputStream extends InputStream {

    final byte first;

    private final InputStream inputStream;

    boolean firstRead = false;

    public AutoConverterSourceInputStream(byte first, InputStream inputStream) {
        this.inputStream = inputStream;
        this.first = first;
    }

    @Override
    public int read() throws IOException {
        if (firstRead) {
            return inputStream.read();
        }
        return first;
    }
}
