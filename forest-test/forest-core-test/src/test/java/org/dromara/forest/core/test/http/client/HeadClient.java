package org.dromara.forest.core.test.http.client;

import org.dromara.forest.annotation.DataVariable;
import org.dromara.forest.annotation.HeadRequest;
import org.dromara.forest.annotation.Header;
import org.dromara.forest.annotation.Query;
import org.dromara.forest.annotation.Request;
import org.dromara.forest.core.test.model.TestHeaders;
import org.dromara.forest.http.ForestResponse;

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
