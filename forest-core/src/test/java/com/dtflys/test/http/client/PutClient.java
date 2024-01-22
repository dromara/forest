package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.annotation.Request;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 17:11
 */
public interface PutClient {

    @Request(
            url = "http://localhost:${port}/hello",
            data = "username=foo&password=123456",
            headers = {"Accept:text/plain"}
    )
    String putHello();

    @Request(
            url = "http://localhost:${port}/hello",
            type = "put",
            data = "username=foo&password=123456",
            headers = {"Accept:text/plain"}
    )
    String simplePut();

    @Put(
            url = "http://localhost:${port}/hello",
            data = "username=foo&password=123456",
            headers = {"Accept:text/plain"}
    )
    String simplePut2();

    @PutRequest(
            url = "http://localhost:${port}/hello",
            data = "username=foo&password=123456",
            headers = {"Accept:text/plain"}
    )
    String simplePut3();

    @Request(
            url = "http://localhost:${port}/hello",
            type = "put",
            data = "username=${0}&password=${1}",
            headers = {"Accept:text/plain"}
    )
    String textParamPut(String username, String password);

    @Request(
            url = "http://localhost:${port}/hello",
            type = "put",
            headers = {"Accept:text/plain"}
    )
    String annParamPut(@Body("username") String username, @Body("password") String password);



}
