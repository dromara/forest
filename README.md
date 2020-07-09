# Forest - 轻量级HTTP客户端访问框架

[![Build Status](https://api.travis-ci.org/mySingleLive/forest.svg?branch=master)](https://travis-ci.org/mySingleLive/forest)
[![license](https://img.shields.io/badge/license-MIT%20License-blue.svg)](https://opensource.org/licenses/mit-license.php)
[![Maven Central](https://img.shields.io/badge/maven%20central-1.2.0-brightgreen.svg)](https://search.maven.org/artifact/com.dtflys.forest/forest-core/1.2.0/jar)

文档：
-------------------------------------
[中文文档](https://dt_flys.gitee.io/forest) 

项目介绍：
-------------------------------------

Forest是一个高层的、极简的轻量级HTTP调用API框架。<br>
相比于直接使用Httpclient您不再用写一大堆重复的代码了，而是像调用本地方法一样去发送HTTP请求。

项目特点：
-----
* 以Httpclient和OkHttp为后端框架
* 通过调用本地方法的方式去发送Http请求, 实现了业务逻辑与Http协议之间的解耦
* 相比Feign更轻量，不依赖Spring Cloud和任何注册中心
* 支持所有请求方法：GET, HEAD, OPTIONS, TRACE, POST, DELETE, PUT, PATCH
* 支持灵活的模板表达式
* 支持过滤器来过滤传入的数据
* 基于注解、配置化的方式定义Http请求
* 支持Spring和Springboot集成
* JSON字符串到Java对象的自动化解析
* XML文本到Java对象的自动化解析
* JSON、XML或其他类型转换器可以随意扩展和替换
* 支持JSON转换框架: Fastjson, Jackson, Gson
* 支持JAXB形式的XML转换
* 可以通过OnSuccess和OnError接口参数实现请求结果的回调
* 配置简单，一般只需要@Request一个注解就能完成绝大多数请求的定义
* 支持异步请求调用

Quick Start
-------------------------------------
以下例子基于Springboot

### 依赖

直接添加以下maven依赖即可

```xml
<dependency>
    <groupId>com.dtflys.forest</groupId>
    <artifactId>spring-boot-starter-forest</artifactId>
    <version>1.2.0</version>
</dependency>
```

### 配置

在`application.properties`中加入:

```properties
forest.enabled = true
```

或者在`application.yml`中加入：

```yaml
forest:
    enabled: true
```

### 一个栗子：访问百度短链接REST接口

### 创建一个`interface`作为远程调用接口


```java

package com.yoursite.client;

import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.annotation.DataParam;

public interface MyClient {

    /**
     * 百度短链接API
     * @param url
     * @return
     */
    @Request(
        url = "http://dwz.cn/create.php",
        type = "post",
        dataType = "json"
    )
    Map getShortUrl(@DataParam("url") String url);
}

```

### 扫描接口

在Spring Boot的配置类或者启动类上加上`@ForestScan`注解，并在`basePackages`属性里填上远程接口的所在的包名

```java
@SpringBootApplication
@Configuration
@ForestScan(basePackages = "com.yoursite.client")
public class MyApp {
 ...
}
```

### 调用接口

OK，终于可以愉快地调用接口了

```java
@Autowired
private MyClient myClient;


public void testClient() {
    Map result = myClient.getShortUrl("https://gitee.com/dt_flys/forest");
    System.out.println(result);
}
```


### 微信交流群:<br>

![avatar](https://dt_flys.gitee.io/forest/media/wechat_qr.png)

项目协议
--------------------------
The MIT License (MIT)

Copyright (c) 2016 Jun Gong


