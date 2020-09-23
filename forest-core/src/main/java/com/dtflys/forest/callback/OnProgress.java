package com.dtflys.forest.callback;

import com.dtflys.forest.utils.ForestProgress;

/**
 * 回调函数: 文件上传或下载监听传输进度时调用
 *
 * @author gongjun
 * @since 2020-07-26
 */
public interface OnProgress {

    /**
     * 文件上传或下载监听传输进度时调用该方法
     * @param progress Forest进度对象
     */
    void onProgress(ForestProgress progress);
}
