package com.dtflys.forest.springboot.test.multiPool;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author JingYu
 * @date 2024/8/23 下午3:12
 * 多线程池测试
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("multiPool")
@SpringBootTest(classes = MultiPoolTests.class)
@EnableAutoConfiguration
public class MultiPoolTests {

    private static final Logger log = LoggerFactory.getLogger(MultiPoolTests.class);
    @Autowired
    private MultiPoolApi multiPoolApi;

    /**
     * 多异步线程创建测试
     * 执行流程：调用配置了：高、普通、默认 线程池http接口
     * 预期：各个线程池可以创建成功，并打印日志
     */
    @Test
    public void asyncPollCreate() throws ExecutionException, InterruptedException {
        Future<String> stringForestRequest = multiPoolApi.highLevelRequest();
        Future<String> normalLevelRequest = multiPoolApi.normalLevelRequest();
        Future<String> defaultLevelRequest = multiPoolApi.defaultLevelRequest();
        System.out.println(stringForestRequest.get());
        System.out.println(normalLevelRequest.get());
        System.out.println(defaultLevelRequest.get());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 多请求等级测试
     * 真实场景下可能会请求多个接口，每个接口的优先级不一
     * 接口A优先级高，接口B优先级低
     * 需要把接口A丢到更高级别的线程池中去执行
     * 需要把接口B丢到普通级别的线程池中慢慢跑
     * 测试流程：B接口执行多次，入队等待，A接口使用高优先级线程池执行
     * 预期：AB接口互不影响、A接口运行结果穿插在普通接口中间执行
     */
    @Test
    public void multiPoolLevelTest() throws InterruptedException {
        // queueSize=5    maxThreadSize=5
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                new Thread(() -> {
                    Future<String> normalLevelRequest = multiPoolApi.normalLevelRequest();
                    try {
                        normalLevelRequest.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("普通接口执行完成");
                }).start();
            }
        }).start();

        new Thread(() -> {
            // queueSize=5    maxThreadSize=10
            Future<String> highLevelRequest = multiPoolApi.highLevelRequest();
            try {
                highLevelRequest.get();
                System.out.println("高优先接口执行完成");
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).start();

        TimeUnit.SECONDS.sleep(100);
    }

    /**
     * 线程池打满测试
     * 测试流程：创建大于线程池可处理任务总数的任务
     * 预期：触发拒绝策略
     */
    @Test
    public void asyncPoolFullTest() {
        for (int i = 0; i < 10; i++) {
            multiPoolApi.fullLevelRequest();
            log.info("task {} 创建成功", i);
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
