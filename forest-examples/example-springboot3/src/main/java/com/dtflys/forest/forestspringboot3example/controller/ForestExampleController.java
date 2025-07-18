package com.dtflys.forest.forestspringboot3example.controller;

import com.dtflys.forest.callback.OnSuccess;
import com.dtflys.forest.forestspringboot3example.client.Amap;
import com.dtflys.forest.forestspringboot3example.client.Cn12306;
import com.dtflys.forest.forestspringboot3example.client.Gitee;
import com.dtflys.forest.forestspringboot3example.model.Coordinate;
import com.dtflys.forest.forestspringboot3example.model.GiteeBranch;
import com.dtflys.forest.forestspringboot3example.model.GiteeReadme;
import com.dtflys.forest.forestspringboot3example.model.Location;
import com.dtflys.forest.forestspringboot3example.model.Result;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

@RestController
public class ForestExampleController {

    @Resource
    private Amap amap;

    @Resource
    private Gitee gitee;

    @Resource
    private Cn12306 cn12306;


    @GetMapping("/amap/location")
    public Result<Location> amapLocation(@RequestParam BigDecimal longitude, @RequestParam BigDecimal latitude) {
        Result<Location> result = amap.getLocation(longitude.toEngineeringString(), latitude.toEngineeringString());
        return result;
    }

    @GetMapping("/amap/location2")
    public Map amapLocation2(@RequestParam BigDecimal longitude, @RequestParam BigDecimal latitude) {
        Coordinate coordinate = new Coordinate(
                longitude.toEngineeringString(),
                latitude.toEngineeringString());
        Map result = amap.getLocation(coordinate);
        return result;
    }

    @GetMapping("/amap/location3")
    public Map amapLocation3(@RequestParam BigDecimal longitude, @RequestParam BigDecimal latitude) {
        Coordinate coordinate = new Coordinate(
                longitude.toEngineeringString(),
                latitude.toEngineeringString());
        Map result = amap.getLocationByCoordinate(coordinate);
        return result;
    }

    @GetMapping("/gitee")
    public String gitee() {
        String result = gitee.index();
        return result;
    }


    @GetMapping("/gitee/async")
    public String aysncGitee() throws ExecutionException, InterruptedException {
        Future<String> future = gitee.asyncIndex();
        return future.get();
    }

    @GetMapping("/gitee/async2")
    public String aysncGitee2() throws ExecutionException, InterruptedException {
        AtomicReference<String> ref = new AtomicReference<>("");
        CountDownLatch latch = new CountDownLatch(1);
        gitee.asyncIndex2((result, req, res) -> {
            ref.set(result);
            latch.countDown();
        }, (ex, req, res) -> {
            ref.set(ex.getMessage());
            latch.countDown();
        });
        latch.await();
        return ref.get();
    }



    @GetMapping("/12306")
    public String cn12306() {
        ForestResponse<String> response = cn12306.index();
        return response.getResult();
    }


    @GetMapping("/gitee/branches")
    public List<GiteeBranch> giteeBranches(@RequestParam String accessToken,
                                           @RequestParam String owner,
                                           @RequestParam String repo) {
        List<GiteeBranch> branches = gitee.branches(accessToken, owner, repo);
        return branches;
    }

    @GetMapping("/gitee/readme")
    public GiteeReadme giteeReadme(@RequestParam String accessToken,
                                   @RequestParam String owner,
                                   @RequestParam String repo,
                                   @RequestParam String ref) {
        GiteeReadme readme = gitee.readme(accessToken, owner, repo, ref);
        return readme;
    }


}
