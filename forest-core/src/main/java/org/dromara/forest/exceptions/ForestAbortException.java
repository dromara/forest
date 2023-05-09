package org.dromara.forest.exceptions;

import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.pool.ForestRequestPool;

/**
 * Forest请求中断异常
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @since 1.5.22
 */
public class ForestAbortException extends ForestPoolException {


    private final ForestRequestPool pool;

    public ForestAbortException(ForestRequest request, ForestRequestPool pool) {
        super(request, "[Forest] Request was rejected from pool: [max pool size = " + pool.getMaxPoolSize() +
                ", running size = " + pool.getRunningPoolSize() +
                ", max route requests = " + pool.getMaxPoolSizePerRoute() +
                ", route requests = " + request.route().getRequestCount().get() +
                ", max queue size = " + pool.getMaxQueueSize() +
                ", queue size = " + pool.getQueueSize() + "]");
        this.pool = pool;
    }

    public ForestRequestPool getPool() {
        return pool;
    }
}
