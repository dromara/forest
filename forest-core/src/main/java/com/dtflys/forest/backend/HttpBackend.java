package com.dtflys.forest.backend;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;

/**
 * HTTP后端接口
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-20 18:22
 */
public interface HttpBackend {

    /**
     * 获取后端框架名称
     *
     * @return 后端框架名称
     */
    String getName();

    /**
     * 创建HTTP执行器
     *
     * @param request Forest请求对象
     * @param lifeCycleHandler 生命周期处理器
     * @return HTTP执行器
     */
    HttpExecutor createExecutor(ForestRequest request, LifeCycleHandler lifeCycleHandler);

    /**
     * 初始化后端框架
     *
     * @param configuration Forest全局配置
     */
    void init(ForestConfiguration configuration);

    /**
     * HTTP执行器的构建器
     */
    interface HttpExecutorCreator {

        /**
         * 创建HTTP执行器
         *
         * @param connectionManager Forest连接池对象
         * @param request Forest请求对象
         * @param lifeCycleHandler 生命周期处理器
         * @return HTTP执行器
         */
        HttpExecutor createExecutor(ForestConnectionManager connectionManager, ForestRequest request, LifeCycleHandler lifeCycleHandler);
    }

    /**
     * 获取连接池
     *
     * @return {@link ForestConnectionManager}实例
     */
    ForestConnectionManager getConnectionManager();

}
