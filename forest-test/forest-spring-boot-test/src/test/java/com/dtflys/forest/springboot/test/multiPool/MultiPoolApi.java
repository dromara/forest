package com.dtflys.forest.springboot.test.multiPool;

import com.dtflys.forest.annotation.Delete;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Var;

import java.util.concurrent.Future;

public interface MultiPoolApi {

    @Get(value = "https://forest.dtflyx.com/pages/1.5.36/spring_boot_config_items/", async = true,asyncPoolName = "high")
    Future<String> highLevelRequest();


    @Get(value = "https://forest.dtflyx.com/pages/1.5.36/spring_boot_config_items/", async = true,asyncPoolName = "normal")
    Future<String> normalLevelRequest();

    @Get(value = "https://forest.dtflyx.com/pages/1.5.36/spring_boot_config_items/", async = true,asyncPoolName = "fullTest")
    Future<String> fullLevelRequest();

    @Get(value = "https://forest.dtflyx.com/pages/1.5.36/spring_boot_config_items/", async = true)
    Future<String> defaultLevelRequest();


}
