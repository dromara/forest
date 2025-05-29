package com.dtflys.forest.test.http.client;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.http.Lazy;
import com.dtflys.forest.test.model.TestHeaders;

import java.util.Map;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 17:50
 */
public interface HeadClient {

    @HeadRequest(
            url = "http://localhost:{port}/hello/user?username=foo",
            headers = {
                    "Accept: text/plain",
                    "accessToken: 11111111",
                    "test: testquery:dsds",
                    "test2: testquery2: dsds"
            }
    )
    void headHelloUser();

    @HeadRequest(
            url = "http://localhost:{port}/hello/user?username=foo",
            headers = {
                    "test: testquery:dsds",
                    "test2: testquery2: dsds"
            }
    )
    void headHelloUser(@Header("Accept") String accept, @Header("accessToken") String accessToken);

    @HeadRequest(
            url = "http://localhost:{port}/hello/user?username=foo",
            headers = {
                    "test: testquery:dsds",
                    "test2: testquery2: dsds"
            }
    )
    void headHelloUser_Lazy(@Header("Accept") String accept, @Header("accessToken") Lazy<String> accessToken);

    @HeadRequest(
            url = "http://localhost:{port}/hello/user?username=foo",
            headers = {
                    "test: testquery:dsds",
                    "test2: testquery2: dsds"
            }
    )
    void headHelloUserWithDefaultHeaders(
            @Header(name = "Accept", defaultValue = "text/plain") String accept,
            @Header(name = "accessToken", defaultValue = "11111111") String accessToken);



    @HeadRequest(
            url = "http://localhost:{port}/hello/user"
    )
    void headHelloUser(@Header Map<String, Object> headers, @Query("username") String username);

    @HeadRequest(
            url = "http://localhost:{port}/hello/user?username=foo"
    )
    void headHelloUser(@Header TestHeaders headers);


    @HeadRequest(
            url = "http://localhost:{port}/hello/user?username=foo",
            headers = {
                "Accept: text/plain",
                "accessToken: {accessToken}",
                "test: {1}",
                "test2: {2}"
            }
    )
    String simpleHead(@DataVariable("accessToken") String accessToken, String test, String test2);

    @HeadRequest(
            url = "http://localhost:{port}/hello/user?username=foo",
            headers = {
                    "Accept:text/plain",
                    "accessToken:11111111",
                    "test: testquery:dsds",
                    "test2: testquery2: dsds"
            }
    )
    void simpleHead2();

    @HeadRequest(
            url = "http://localhost:{port}/hello/user?username=foo",
            headers = {
                    "Accept:text/plain",
                    "accessToken:11111111",
                    "test: testquery:dsds",
                    "test2: testquery2: dsds"
            }
    )
    void simpleHead3();

    @Request(
            url = "http://localhost:{port}/hello/user?username=foo",
            type = "head",
            headers = {
                "Accept:text/plain",
                "accessToken:11111111",
                "test: testquery:dsds",
                "test2: testquery2: dsds"
            }
    )
    ForestResponse responseHead();

}
