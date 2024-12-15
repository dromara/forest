package com.dtflys.forest.test.jsonpath;

import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.JSONPathResult;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.test.model.TestUser;

import java.util.List;

public interface TestJSONPathClient {

    @Get("http://localhost:{port}/test/user")
    @JSONPathResult("$.data")
    TestUser getSingleUser();

    @Get("http://localhost:{port}/test/user")
    @JSONPathResult("$.data")
    List<TestUser> getListOfUsers();

    @Get("http://localhost:{port}/test/user")
    @JSONPathResult("$.data[*].age")
    List<Integer> getListOfUserAges();


    @Get("http://localhost:{port}/test/user")
    @JSONPathResult("$.data[?(@.age>{minAge})].age")
    List<Integer> getListOfUserAges(@Var("minAge") int minAge);
}
