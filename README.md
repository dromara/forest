# Forest - 轻量级HTTP客户端访问框架


[![Build Status](https://api.travis-ci.org/mySingleLive/forest.svg?branch=master)](https://travis-ci.org/mySingleLive/forest)
[![license](https://img.shields.io/badge/license-MIT%20License-blue.svg)](https://opensource.org/licenses/mit-license.php)
[![Maven Central](https://img.shields.io/badge/maven%20central-1.0.0-brightgreen.svg)](https://search.maven.org/search?q=g:com.dtflys.forest)

项目介绍：
-------------------------------------

Forest是一个高层的、极简的轻量级HTTP调用API框架。<br>
相比于直接使用Httpclient您不再用写一大堆重复的代码了，而是像调用本地方法一样去发送HTTP请求。

项目特点：
-----
* 以Httpclient和OkHttp为后端框架
* 通过调用本地方法的方式去发送Http请求
* 相比Feign更轻量，不依赖Spring Cloud和任何注册中心
* 支持灵活的模板表达式
* 支持过滤器来过滤传入的数据
* 基于注解、配置化的方式定义Http请求
* 支持Spring集成：除了基本信息配置，它能自动扫描、注入到你自己的代码中
* JSON字符串到Java对象的自动化解析
* XML文本到Java对象的自动化解析
* JSON、XML或其他类型转换器可以随意扩展和替换
* 可以通过OnSuccess和OnError接口参数实现请求结果的回调
* 配置简单，一般只需要@Request一个注解就能完成绝大多数请求的定义
* 实现了业务逻辑与Http协议之间的解耦
* 支持所有请求方法：GET, HEAD, OPTIONS, TRACE, POST, DELETE, PUT, PATCH
* 支持JSON转换框架: Fastjson, Jackson, Gson
* 支持JAXB形式的XML转换
* 支持Spring和Springboot集成
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

application.properties
```properties
forest.enabled = true
```

或者在application.yml
```yaml
forest:
    enabled: true
```

### 一个栗子：访问百度短链接REST接口

### 创建一个Interface作为远程调用接口


```java

package com.yourproject.pkg;

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

在Springboot的配置类或者启动类上加上@ForestScan注解，并在basePackages属性里填上远程接口的所在的包名

```java

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import com.dtflys.forest.annotation.ForestScan;

@SpringBootApplication
@Configuration
@ForestScan(basePackages = "com.yourproject.pkg")
public class MyConfiguration {
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


### 详细文档:<br>
* [HTTP请求](forest-core/src/main/doc/REQUEST.md)<br>
* [变量/参数](forest-core/src/main/doc/DOCUMENTATION.md)<br>
* [回调方法](forest-core/src/main/doc/CALLBACK.md)<br>
* [在Spring中使用](forest-core/src/main/doc/SPRING.md)<br>
* [拦截器](forest-core/src/main/doc/INTERCEPTOR.md)


### 技术支持:<br>

QQ群：930773917

项目协议
--------------------------
The MIT License (MIT)

Copyright (c) 2016 Jun Gong


