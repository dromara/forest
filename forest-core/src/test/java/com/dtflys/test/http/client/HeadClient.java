package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.model.TestHeaders;

import java.util.Map;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 17:50
 */
public interface HeadClient {

    @HeadRequest(
            url = "http://localhost:${port}/hello/user?username=foo",
            headers = {
                    "Accept:text/plain",
                    "accessToken:11111111"
            }
    )
    void headHelloUser();

    @HeadRequest(
            url = "http://localhost:${port}/hello/user?username=foo"
    )
    void headHelloUser(@Header("Accept") String accept, @Header("accessToken") String accessToken);

    @HeadRequest(
            url = "http://localhost:${port}/hello/user"
    )
    void headHelloUser(@Header Map<String, Object> headers, @Query("username") String username);

    @HeadRequest(
            url = "http://localhost:${port}/hello/user?username=foo"
    )
    void headHelloUser(@Header TestHeaders headers);


    @HeadRequest(
            url = "http://localhost:${port}/hello/user?username=foo",
            headers = {
                "Accept: text/plain",
                "accessToken: ${accessToken}"
            }
    )
    void simpleHead(@DataVariable("accessToken") String accessToken);

    @HeadRequest(
            url = "http://localhost:${port}/hello/user?username=foo",
            headers = {
                    "Accept:text/plain",
                    "accessToken:11111111"
            }
    )
    void simpleHead2();

    @HeadRequest(
            url = "http://localhost:${port}/hello/user?username=foo",
            headers = {
                    "Accept:text/plain",
                    "accessToken:11111111"
            }
    )
    void simpleHead3();

    @Request(
            url = "http://localhost:${port}/hello/user?username=foo",
            type = "head",
            headers = {
                "Accept:text/plain",
                "accessToken:11111111"
            }
    )
    ForestResponse responseHead();

}
