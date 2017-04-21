package org.forest.client;

import org.forest.model.ShortUrlResult;
import org.forest.annotation.Request;
import org.forest.annotation.DataParam;

import java.util.Map;

/**
 * Created by Administrator on 2016/3/24.
 */
public interface WwwClient {


/*
    @GET("http://222.73.117.138:7891/mt")
    public String sendMessage(@DataParam("un") String userName,
                              @DataParam("pw") String password,
                              @DataParam(value = "da", encoding = "UTF-8") String phoneAddress,
                              @DataParam("sm") String content);
*/


/*
    @Request(
            url = "${0}/send?un=${1}&pw=${2}&da=${3}&sm=${4}",
            type = "get",
            dataType = "json"
    )
    public Map<String, Object> send(
            String base,
            String userName,
            String password,
            String phoneList,
            String content
    );
*/

/*
    @Request(
        url = "http://assets.imgix.net/examples/puffins.jpg?auto=format&fit=crop&fm=png8&h=${h}&ixjsv=2.2.3&mask=%2Fix-mask.png&q=90&rot=180&w=${w}",
        type = "get"
    )
    void imgix(@DataVariable("w") Integer width, @DataVariable("h") Integer height, OnSuccess onSuccess);
*/


    @Request(
        url = "http://dwz.cn/create.php",
        type = "post",
        dataType = "json",
        headers = {
            "Cache-control: no-cache"
        }
    )
    Map dwz(@DataParam("url") String url);


    /**
     * 百度短链接API
     * @param url
     * @return
     */
    @Request(
        url = "http://dwz.cn/create.php?ver=${1}",
        type = "post",
        dataType = "json",
        headers = {
            "Cache-control:no-store",
            "Pragma:no-cache"
        }
    )
    ShortUrlResult getShortUrl(@DataParam("url") String url);


    @Request(
        url = "${baseShortUrl}/create.php",
        type = "post",
        dataType = "json",
        headers = {
            "Cache-control: no-cache"
        }
    )
    Map testVar(@DataParam("url") String url);



}
