package org.forest.client;

import org.forest.annotation.Request;
import org.forest.annotation.DataVariable;

import java.util.List;

/**
 * BosonNLP HTTP API 客户端
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-02-21 17:49
 */
public interface BosonClient {

    /**
     * 语义联想接口
     * @param word 词语
     * @param top  最前几个关联词的数量
     * @return
     */
    @Request(
            url = "http://api.bosonnlp.com/suggest/analysis?top_k={1}",
            type = "post",
            contentEncoding = "utf-8",
            contentType = "application/json",
            headers = {
                    "Content-Type:application/json",
                    "Accept:application/json",
                    "X-Token:2HtnxSn0.13206.njpecLOnJHTc"
            },
            dataType = "json",
            data = "\"${word}\""
    )
    List<List> suggest(@DataVariable("word") String word, Integer top);


}
