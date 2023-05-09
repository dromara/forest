package org.dromara.forest.example.controller;

import org.dromara.forest.example.client.Amap;
import org.dromara.forest.example.client.Cn12306;
import org.dromara.forest.example.client.Gitee;
import org.dromara.forest.example.model.*;
import org.dromara.forest.http.ForestResponse;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

@Controller
public class ForestExampleController {

    @Inject
    private Amap amap;

    @Inject
    private Gitee gitee;

    @Inject
    private Cn12306 cn12306;


    @Get
    @Mapping("/amap/location")
    public Result<Location> amapLocation(BigDecimal longitude, BigDecimal latitude) {
        Result<Location> result = amap.getLocation(longitude.toEngineeringString(), latitude.toEngineeringString());
        return result;
    }

    @Get
    @Mapping("/amap/location2")
    public Map amapLocation2(BigDecimal longitude, BigDecimal latitude) {
        Coordinate coordinate = new Coordinate(
                longitude.toEngineeringString(),
                latitude.toEngineeringString());
        Map result = amap.getLocation(coordinate);
        return result;
    }

    @Get
    @Mapping("/amap/location3")
    public Map amapLocation3(BigDecimal longitude, BigDecimal latitude) {
        Coordinate coordinate = new Coordinate(
                longitude.toEngineeringString(),
                latitude.toEngineeringString());
        Map result = amap.getLocationByCoordinate(coordinate);
        return result;
    }

    @Get
    @Mapping("/gitee")
    public String gitee() {
        String result = gitee.index();
        return result;
    }


    @Get
    @Mapping("/gitee/async")
    public String aysncGitee() throws ExecutionException, InterruptedException {
        Future<String> future = gitee.asyncIndex();
        return future.get();
    }

    @Get
    @Mapping("/gitee/async2")
    public String aysncGitee2() throws ExecutionException, InterruptedException {
        AtomicReference<String> ref = new AtomicReference<>("");
        CountDownLatch latch = new CountDownLatch(1);
        gitee.asyncIndex2((result, request, response) -> {
            ref.set(result);
            latch.countDown();
        }, (ex, request, response) -> {
            ref.set(ex.getMessage());
            latch.countDown();
        });
        latch.await();
        return ref.get();
    }


    @Get
    @Mapping("/12306")
    public String cn12306() {
        ForestResponse<String> response = cn12306.index();
        return response.getResult();
    }


    @Get
    @Mapping("/gitee/branches")
    public List<GiteeBranch> giteeBranches(String accessToken,
                                           String owner,
                                           String repo) {
        List<GiteeBranch> branches = gitee.branches(accessToken, owner, repo);
        return branches;
    }

    @Get
    @Mapping("/gitee/readme")
    public GiteeReadme giteeReadme(String accessToken,
                                   String owner,
                                   String repo,
                                   String ref) {
        GiteeReadme readme = gitee.readme(accessToken, owner, repo, ref);
        return readme;
    }
}
