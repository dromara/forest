package com.dtflys.forest.http;

import com.dtflys.forest.exceptions.ForestRuntimeException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class ForestHttpInputStream extends InputStream {

    private final InputStream inputStream;

    public ForestHttpInputStream(ForestResponse<?> response) {
        try {
            InputStream in = response.getRawInputStream();
            if (response.isGzip() && !(in instanceof GZIPInputStream)) {
                in = new GZIPInputStream(in);
            }
            if (response.isDeflate() && !(in instanceof InflaterInputStream)) {
                in = new InflaterInputStream(in);
            }
            this.inputStream = in;
        } catch (Exception e) {
            if (e instanceof ForestRuntimeException) {
                throw (ForestRuntimeException) e;
            }
            throw new ForestRuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    @Override
    public int available() throws IOException {
        return inputStream.available();
    }

    @Override
    public long skip(long n) throws IOException {
        return inputStream.skip(n);
    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return inputStream.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return inputStream.read(b, off, len);
    }

    @Override
    public int readNBytes(byte[] b, int off, int len) throws IOException {
        return inputStream.readNBytes(b, off, len);
    }

    @Override
    public synchronized void mark(int readlimit) {
        inputStream.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        inputStream.reset();
    }

    @Override
    public boolean markSupported() {
        return inputStream.markSupported();
    }

    @Override
    public long transferTo(OutputStream out) throws IOException {
        return inputStream.transferTo(out);
    }


}
