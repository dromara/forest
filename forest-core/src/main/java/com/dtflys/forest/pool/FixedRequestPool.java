package com.dtflys.forest.pool;


import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestAbortException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestRoute;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 固定大小请求池
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @since 1.5.22
 */
public class FixedRequestPool implements ForestRequestPool {
    private volatile Integer maxPoolSize;

    private volatile Integer maxPoolSizePerRoute;

    private volatile Integer maxQueueSize;

    private AtomicInteger runningPoolSize = new AtomicInteger(0);

    private Queue<ForestRequest> queue;


    public FixedRequestPool(ForestConfiguration configuration) {
        maxPoolSize = configuration.getMaxConnections();
        if (maxPoolSize == null) {
            maxPoolSize = DEFAULT_POOL_SIZE;
        }
        maxPoolSizePerRoute = configuration.getMaxRouteConnections();
        if (maxPoolSizePerRoute == null) {
            maxPoolSizePerRoute = DEFAULT_POOL_SIZE_PER_ROUTE;
        }
        maxQueueSize = configuration.getMaxRequestQueueSize();
        if (maxQueueSize == null) {
            maxQueueSize = DEFAULT_QUEUE_SIZE;
        }
        if (maxQueueSize == 0) {
            queue = new SynchronousQueue<>();
        } else {
            queue = new LinkedBlockingDeque<>(maxQueueSize);
        }
    }

    /**
     * 提交请求
     *
     * @param request Forest请求对象
     */
    @Override
    public synchronized void awaitRequest(ForestRequest request) {
        ForestRoute route = request.route();
        // 判断当前活动请求数是否小于最大请求池大小
        // 并且当前活动的路由请求数是否小于最大每路由请求数
        if (runningPoolSize.get() < maxPoolSize &&
                route.getRequestCount().get() < maxPoolSizePerRoute) {
            // 增加当前活动请求数
            runningPoolSize.incrementAndGet();
            // 增加当前活动路由请求数
            route.getRequestCount().incrementAndGet();
            return;
        }
        // 加入请求等待队列
        boolean queued = false;
        try {
            queue.add(request);
            queued = true;
        } catch (Throwable th) {
            // 如果请求等待队列也满了，则抛出异常
            throw new ForestAbortException(request, this);
        } finally {
            if (!queued) {
                notifyAll();
            }
        }
        // 阻塞同步请求线程，直到当前活动请求大小小于最大请求池大小为止
        while (runningPoolSize.get() >= maxPoolSize ||
                route.getRequestCount().get() >= maxPoolSizePerRoute) {
            try {
                this.wait(10);
            } catch (InterruptedException e) {
                throw new ForestRuntimeException(e);
            }
        }
        // 阻塞结束，说明有空余空间
        // 则从请求等待队列中删除当前请求
        queue.remove(request);
        // 增加当前活动请求数
        runningPoolSize.incrementAndGet();
        // 增加当前活动路由请求数
        route.getRequestCount().incrementAndGet();
    }

    /**
     * 请求完成
     *
     * @param request Forest请求对象
     */
    @Override
    public synchronized void finish(ForestRequest request) {
        // 减少当前活动请求数
        runningPoolSize.decrementAndGet();
        if (runningPoolSize.get() == 0) {
            runningPoolSize.set(0);
        }
        // 减少当前活动路由请求数
        request.route().getRequestCount().decrementAndGet();
        // 通知其他线程
        this.notifyAll();
    }

    @Override
    public Integer getRunningPoolSize() {
        return runningPoolSize.get();
    }

    @Override
    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    @Override
    public Integer getMaxPoolSizePerRoute() {
        return maxPoolSizePerRoute;
    }

    @Override
    public Integer getMaxQueueSize() {
        return maxQueueSize;
    }

    @Override
    public Integer getQueueSize() {
        return queue.size();
    }
}
