package com.dtflys.forest.backend

import com.dtflys.forest.config.ForestConfiguration
import com.dtflys.forest.handler.LifeCycleHandler
import com.dtflys.forest.http.ForestRequest
import com.dtflys.forest.http.ForestResponseFactory
import com.dtflys.forest.reflection.MethodLifeCycleHandler
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

/**
 * Forest异步携程请求执行器
 * @author CHMing
 * @since 1.5.23
 **/
class ChannelExecutorService(

    protected val configuration: ForestConfiguration,

    /**
     * Forest同步请求执行器
     */
    protected val syncExecutor: HttpExecutor,

    /**
     * Forest响应对象处理器
     */
    protected val handler: ResponseHandler<Any>
) : AbstractExecutorService(), HttpExecutor {

    override fun execute(command: Runnable) {
        channel?.runBlock {
            command.run()
        }
    }

    fun isEmpty(): Boolean {
        return channel?.let {
            return@let it.isEmpty || it.isClosedForReceive
        } ?: false
    }

    override fun shutdown() {
        channel?.close()
    }

    override fun shutdownNow(): MutableList<Runnable> {
        shutdown()
        return mutableListOf()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun isShutdown(): Boolean {
        return channel?.isClosedForSend ?: true
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun isTerminated(): Boolean {
        return channel?.isClosedForReceive ?: true
    }

    override fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean {
        var millis = unit.toMillis(timeout)
        while (!isTerminated && millis > 0) {
            try {
                Thread.sleep(200L)
                millis -= 200L
            } catch (ignore: Exception) {
            }
        }
        return isTerminated
    }

    override fun getRequest(): ForestRequest<*> = syncExecutor.request

    override fun execute(lifeCycleHandler: LifeCycleHandler?) {
        if (channel == null) {
            synchronized(this) {
                channel = Channel(configuration.maxAsyncQueueSize ?: DEFAULT_MAX_COROUTINE_SIZE)
            }
        }
        val future: CompletableFuture<Any> = CompletableFuture()
        handler.handleFuture(future)

        channel?.runBlock {
            syncExecutor.execute(lifeCycleHandler)
            val result = if (lifeCycleHandler is MethodLifeCycleHandler<*>) {
                lifeCycleHandler.resultData
            } else null
            future.complete(result)
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