package org.forest.client;

import org.forest.model.ShortUrlResult;
import org.forest.annotation.Request;
import org.forest.annotation.DataParam;

import java.util.Map;

/**
 * Created by Administrator on 2016/3/24.
 */
public interface WwwClient {



    @Request(
            url = "${baseUrl}/aol/search?s_it=newtab&v_t=comsearch-nt&q=${0}",
            type = "get"
    )
    public String aolSearch(String keyword);



}
