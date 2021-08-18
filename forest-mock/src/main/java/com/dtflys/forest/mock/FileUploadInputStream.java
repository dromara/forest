package com.dtflys.forest.mock;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author gongjun
 */
public class FileUploadInputStream extends ServletInputStream {
    private final InputStream inputStream;

    private final int readLimit;

    public FileUploadInputStream(InputStream inputStream, int readLimit) {
        this.inputStream = inputStream;
        this.readLimit = readLimit;
    }

    @Override
    public int read() throws IOException {
        return this.inputStream.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (this.readLimit > -1) {
            return this.inputStream.read(b, off, Math.min(this.readLimit, len));
        }
        return this.inputStream.read(b, off, len);
    }

    @Override
    public boolean isFinished() {
        try {
            return this.inputStream.available() == 0;
        } catch (Throwable th) {
            throw new RuntimeException(th);
        }
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
    }
}
