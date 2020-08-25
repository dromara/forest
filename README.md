


<h1 align="center">Forest - 轻量级HTTP客户端框架</h1>

<p align="center">
<a href="https://travis-ci.org/github/mySingleLive/forest">
    <img src="https://api.travis-ci.org/mySingleLive/forest.svg?branch=master&status=passed" alt="Build">
</a>
<a href="https://search.maven.org/artifact/com.dtflys.forest/forest-core/1.4.0/jar">
    <img src="https://img.shields.io/badge/maven%20central-1.4.0-brightgreen.svg" alt="Maven Central">
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

Forest有哪些特性？
-----
* 以Httpclient和OkHttp为后端框架
* 通过调用本地方法的方式去发送Http请求, 实现了业务逻辑与Http协议之间的解耦
* 相比Feign更轻量，不依赖Spring Cloud和任何注册中心
* 支持所有请求方法：GET, HEAD, OPTIONS, TRACE, POST, DELETE, PUT, PATCH
* 支持文件上传和下载
* 支持灵活的模板表达式
* 支持拦截器处理请求的各个生命周期
* 支持自定义注解
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
    <version>1.4.0</version>
</dependency>
```



### 第二步：创建一个`interface`

就以高德地图API为栗子吧

```java

package com.yoursite.client;

import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.annotation.DataParam;

public interface AmapClient {

    /**
     * 聪明的你一定看出来了@Get注解代表该方法专做GET请求
     * 在url中的${0}代表引用第一个参数，${1}引用第二个参数
     */
    @Get(url = "http://ditu.amap.com/service/regeo?longitude=${0}&latitude=${1}")
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

## 文件上传

```java
/**
 * 用@DataFile注解修饰要上传的参数对象
 * OnProgress参数为监听上传进度的回调函数
 */
@Post(url = "/upload")
Map upload(@DataFile("file") String filePath, OnProgress onProgress);
```

可以用一个方法加Lambda同时解决文件上传和上传的进度监听

```java
Map result = myClient.upload("D:\\TestUpload\\xxx.jpg", progress -> {
    System.out.println("progress: " + Math.round(progress.getRate() * 100) + "%");  // 已上传百分比
    if (progress.isDone()) {   // 是否上传完成
        System.out.println("--------   Upload Completed!   --------");
    }
});
```

## 文件下载

下载文件也是同样的简单

```java
/**
 * 在方法上加上@DownloadFile注解
 * dir属性表示文件下载到哪个目录
 * filename属性表示文件下载成功后以什么名字保存，如果不填，这默认从URL中取得文件名
 * OnProgress参数为监听上传进度的回调函数
 * ${0}代表引用第一个参数，${1}引用第二个参数
 */
@Get(url = "http://localhost:8080/images/xxx.jpg")
@DownloadFile(dir = "${0}", filename = "${1}")
File downloadFile(String dir, String filename, OnProgress onProgress);
```


调用下载接口以及监听上传进度的代码如下：

```java
File file = myClient.downloadFile("D:\\TestDownload", progress -> {
    System.out.println("progress: " + Math.round(progress.getRate() * 100) + "%");  // 已下载百分比
    if (progress.isDone()) {   // 是否下载完成
        System.out.println("--------   Download Completed!   --------");
    }
});
```



#### 详细文档请看：[dt_flys.gitee.io/forest](https://dt_flys.gitee.io/forest)

### 联系作者:<br>

亲，进群前记得先star一下哦~

扫描二维码关注公众号，点击菜单中的 `进群` 按钮即可进群

![avatar](https://dt_flys.gitee.io/forest/media/wx_gzh.jpg)

参与贡献
-----------------------------------

1. 进群讨论，可以在群里抛出您遇到的问题，或许已经有人解决了您的问题。
2. 提issue，如果在gitee的issue中已经有您想解决的问题，可以直接将该issue分配给您自己。如若没有，可以自己在gitee上创建一个issue。
3. Fork 本项目的仓库
4. 新建分支，如果是加新特性，分支名格式为`feat_${issue的ID号}`，如果是修改bug，则命名为`fix_${issue的ID号}`。
5. 本地自测，提交前请通过所有的已经单元测试，以及为您要解决的问题新增单元测试。
6. 提交代码
7. 新建 Pull Request
8. 我会对您的PR进行验证和测试，如通过测试，我会合到`dev`分支上随新版本发布时再合到`master`分支上。

欢迎小伙伴们多提issue和PR，被接纳PR的小伙伴我会列在`README`上的贡献者列表中:）

项目协议
--------------------------
The MIT License (MIT)

Copyright (c) 2016 Jun Gong


