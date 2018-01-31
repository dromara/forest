package org.forest.http.client;

import org.forest.annotation.DataVariable;
import org.forest.annotation.Request;
import org.forest.callback.OnSuccess;

import java.util.Map;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-06-06 15:54
 */
public interface CallbackClient {

    @Request(
            url = "http://localhost:5000/hello/user",
            headers = {"Accept:text/plan"},
            data = "username=${username}"
    )
    String getOnSuccess(@DataVariable("username") String username, OnSuccess<String> onSuccess);


    @Request(
            url = "http://localhost:5000/hello/user",
            headers = {"Accept:text/plan"},
            data = "username=${username}",
            dataType = "json"
    )
    String getOnSuccessMap(@DataVariable("username") String username, OnSuccess<Map> onSuccess);


}
