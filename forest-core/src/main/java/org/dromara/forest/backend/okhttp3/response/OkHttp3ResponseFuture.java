package org.dromara.forest.backend.okhttp3.response;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-28 18:10
 */
public class OkHttp3ResponseFuture implements Future<Object> {

    private volatile boolean cancelled;
    private volatile boolean completed;
    private volatile Object result;
    private volatile Exception ex;


    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean isDone() {
        return completed;
    }

    private Object getResult() throws ExecutionException {
        if (this.ex != null) {
            throw new ExecutionException(this.ex);
        }
        return this.result;
    }


    @Override
    public synchronized Object get() throws InterruptedException, ExecutionException {
        while (!this.completed) {
            wait();
        }
        return getResult();
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        final long msecs = unit.toMillis(timeout);
        final long startTime = (msecs <= 0) ? 0 : System.currentTimeMillis();
        long waitTime = msecs;
        if (this.completed) {
            return getResult();
        } else if (waitTime <= 0) {
            throw new TimeoutException();
        } else {
            for (;;) {
                synchronized (this) {
                    wait(waitTime);
                    if (this.completed) {
                        return getResult();
                    } else {
                        waitTime = msecs - (System.currentTimeMillis() - startTime);
                        if (waitTime <= 0) {
                            throw new TimeoutException();
                        }
                    }
                }
            }
        }

    }

    public boolean completed(final Object response) {
        synchronized(this) {
            if (this.completed) {
                return false;
            }
            this.completed = true;
            this.result = response;
            notifyAll();
        }
        return true;
    }

    public boolean failed(final Exception exception) {
        synchronized(this) {
            if (this.completed) {
                return false;
            }
            this.completed = true;
            this.ex = exception;
            notifyAll();
        }
        return true;
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        synchronized(this) {
            if (this.completed) {
                return false;
            }
            this.completed = true;
            this.cancelled = true;
            notifyAll();
        }
        return true;
    }

    public boolean cancel() {
        return cancel(true);
    }

}
