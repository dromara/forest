


<h1 align="center">Forest - 轻量级HTTP客户端框架</h1>

<p align="center">
<a href="https://travis-ci.org/github/mySingleLive/forest">
    <img src="https://api.travis-ci.org/mySingleLive/forest.svg?branch=master&status=passed" alt="Build">
</a>
<a href="https://search.maven.org/artifact/com.dtflys.forest/forest-core/1.3.11/jar">
    <img src="https://img.shields.io/badge/maven%20central-1.3.11-brightgreen.svg" alt="Maven Central">
</a>
<a href="https://codecov.io/gh/mySingleLive/forest">
    <img src="https://codecov.io/gh/mySingleLive/forest/branch/master/graphs/badge.svg?branch=master" alt="Codecov">
</a>
<a href="https://opensource.org/licenses/mit-license.php">
    <img src="https://img.shields.io/badge/license-MIT%20License-blue.svg" alt="License">
</a>
</p>


项目介绍：
-------------------------------------

Forest是一个高层的、极简的轻量级HTTP调用API框架。<br>
相比于直接使用Httpclient您不再用写一大堆重复的代码了，而是像调用本地方法一样去发送HTTP请求。

文档和示例：
-------------------------------------
* [中文文档](https://dt_flys.gitee.io/forest) 

* [Demo工程](https://gitee.com/dt_flys/forest-example)

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

极速开始
-------------------------------------
以下例子基于Spring Boot

### 第一步：添加Maven依赖

直接添加以下maven依赖即可

```xml
<dependency>
    <groupId>com.dtflys.forest</groupId>
    <artifactId>spring-boot-starter-forest</artifactId>
    <version>1.3.11</version>
</dependency>
```



### 第二步：创建一个`interface`

就以高德地图API为栗子吧

```java

package com.yoursite.client;

import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.annotation.DataParam;

public interface AmapClient {

    @Request(
        url = "http://ditu.amap.com/service/regeo?longitude=${0}&latitude=${1}",
        dataType = "json"
    )
    Map getLocation(String longitude, String latitude);
}

```

### 第三步：扫描接口

在Spring Boot的配置类或者启动类上加上`@ForestScan`注解，并在`basePackages`属性里填上远程接口的所在的包名

```java
@SpringBootApplication
@Configuration
@ForestScan(basePackages = "com.yoursite.client")
public class MyApplication {
  public static void main(String[] args) {
      SpringApplication.run(MyApplication.class, args);
   }
}
```

### 第四步：调用接口

OK，我们可以愉快地调用接口了

```java
// 注入接口实例
@Autowired
private AmapClient amapClient;
...
// 调用接口
Map result = amapClient.getLocation("121.475078", "31.223577");
System.out.println(result);
```

#### 详细文档请看：[dt_flys.gitee.io/forest](https://dt_flys.gitee.io/forest)

联系作者:
-----------------------------------

亲，进群前记得先 <font color="yellow">star</font> 一下哦~

扫描二维码关注公众号，回复关键字 `forest` 即可加群

![avatar](https://dt_flys.gitee.io/forest/media/wechat_qr.png)


参与贡献
-----------------------------------

1. 提issue，如果在gitee的issue中已经有您想解决的问题，可以直接将该issue分配给您自己。如若没有，可以自己在gitee上创建一个issue。
2. Fork 本项目的仓库
3. 新建分支，分支名格式为`feat_${issue的ID号}`
4. 本地自测，提交前请通过所有的已经单元测试，以及为您要解决的问题新增单元测试。
5. 提交代码
6. 新建 Pull Request
7. 我会对您的PR进行验证和测试，如通过测试，我会合到`dev`分支上随新版本发布时再合到`master`分支上。
8. 欢迎小伙伴们多提issue和PR，被接纳PR的小伙伴我会列在`README`上的贡献者列表中:）

项目协议
-----------------------------------
The MIT License (MIT)

Copyright (c) 2016 Jun Gong


