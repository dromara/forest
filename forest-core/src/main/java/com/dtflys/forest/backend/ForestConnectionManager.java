package com.dtflys.forest.backend;

import com.dtflys.forest.config.ForestConfiguration;

/**
 * Forest连接管理器
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-27 15:51
 */
public interface ForestConnectionManager {

    boolean isInitialized();
    void init(ForestConfiguration configuration);
}
