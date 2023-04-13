package org.dromara.forest.backend

import org.dromara.forest.config.ForestConfiguration
import org.dromara.forest.handler.LifeCycleHandler
import org.dromara.forest.http.ForestFuture
import org.dromara.forest.http.ForestRequest
import org.dromara.forest.http.ForestResponse
import org.dromara.forest.http.ForestResponseFactory
import org.dromara.forest.reflection.MethodLifeCycleHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.util.concurrent.CompletableFuture

/**
 * Forest异步携程请求执行器
 * @author CHMing
 * @since 1.5.23
 **/
class KotlinCoroutineHttpExecutor (

    protected val configuration: ForestConfiguration,

    /**
     * Forest同步请求执行器
     */
    protected val syncExecutor: HttpExecutor,

    /**
     * Forest响应对象处理器
     */
    protected val handler: ResponseHandler<Any>
) : HttpExecutor {

    override fun getRequest(): ForestRequest<Any> = syncExecutor.request

    override fun execute(lifeCycleHandler: LifeCycleHandler?) {
        if (channel == null) {
            synchronized(this) {
                channel = Channel(configuration.maxAsyncQueueSize ?: DEFAULT_MAX_COROUTINE_SIZE)
            }
        }
        val future: CompletableFuture<ForestResponse<Any>> = CompletableFuture()
        val forestFuture: ForestFuture<Any> = ForestFuture(request, future)
        handler.handleFuture(request, forestFuture)

        channel?.runBlock {
            syncExecutor.execute(lifeCycleHandler)
            val res: ForestResponse<Any>? = if (lifeCycleHandler is MethodLifeCycleHandler<*>) {
                lifeCycleHandler.response as ForestResponse<Any>
            } else null
            future.complete(res)
        }
    }

    override fun getResponseHandler(): ResponseHandler<Any> = handler

    override fun getResponseFactory(): ForestResponseFactory<*> = syncExecutor.responseFactory

    override fun close() {

    }

    companion object {

        const val DEFAULT_MAX_COROUTINE_SIZE = 200

        var channel: Channel<Any>? = null

        /**
         * 关闭异步请求线程池
         *
         * @since 1.5.23
         */
        @Synchronized
        fun closePool() {
            channel?.close()
        }


        /**
         * 重启异步请求线程池
         *
         * @since 1.5.23
         */
        @Synchronized
        fun restartPool() {
            channel?.close()
            channel = null
        }
    }
}

fun Channel<Any>.runBlock(block: suspend CoroutineScope.() -> Unit) {
    CoroutineScope(Dispatchers.Unconfined).launch {
        send(0)
        CoroutineScope(Dispatchers.IO).launch {
            block()
            receive()
        }
    }
}
