

<p align="center">
<a href="http://forest.dtflyx.com/">
    <img width="300" src="site/media/logo3.png" alt="logo">
</a>
</p>


<p align="center">
<a href="https://gitee.com/dromara/forest">
    <img src="https://gitee.com/dromara/forest/badge/star.svg" alt="Gitee Stars">
</a>
<!--
<a href="https://search.maven.org/artifact/com.dtflys.forest/forest-core/1.5.2-BETA/jar">
    <img src="https://img.shields.io/badge/maven%20central-1.5.1-brightgreen.svg" alt="Maven Central">
</a>
-->
<!--
<a href="https://gitee.com/dromara/forest/releases/v1.5.2-BETA">
    <img src="https://img.shields.io/badge/release-v1.5.2-BETA" alt="Release">
</a>
-->
<a href="https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html">
    <img src="https://img.shields.io/badge/JDK-1.8+-yellow" alt="JDK">
</a>
<a href="https://codecov.io/gh/dromara/forest">
    <img src="https://codecov.io/gh/dromara/forest/branch/master/graphs/badge.svg?branch=master" alt="Codecov">
</a>
<a href="https://opensource.org/licenses/mit-license.php">
    <img src="https://img.shields.io/badge/license-MIT-blue.svg" alt="License">
</a>
<a href="http://forest.dtflyx.com/">
    <img src="https://img.shields.io/badge/document-1.x-e96.svg" alt="Documentation">
</a>
<a href="https://gitee.com/dromara/forest#%E8%81%94%E7%B3%BB%E4%BD%9C%E8%80%85">
    <img src="https://img.shields.io/badge/author-%E5%85%AC%E5%AD%90%E9%AA%8F-7af" alt="Author">
</a>
</p>

<h1 align="center">Forest - 轻量级HTTP客户端框架</h1>


项目介绍：
-------------------------------------

Forest是一个高层的、极简的轻量级HTTP调用API框架。<br>
相比于直接使用Httpclient您不再用写一大堆重复的代码了，而是像调用本地方法一样去发送HTTP请求。

文档和示例：
-------------------------------------
* [项目主页](http://forest.dtflyx.com/) 

* [中文文档](http://forest.dtflyx.com/docs/) 

* [JavaDoc](https://apidoc.gitee.com/dt_flys/forest/)

* [Demo工程](https://gitee.com/dt_flys/forest-example)

Forest有哪些特性？
-----
* 以Httpclient和OkHttp为后端框架
* 通过调用本地方法的方式去发送Http请求, 实现了业务逻辑与Http协议之间的解耦
* 因为针对第三方接口，所以不需要依赖Spring Cloud和任何注册中心
* 支持所有请求方法：GET, HEAD, OPTIONS, TRACE, POST, DELETE, PUT, PATCH
* 支持文件上传和下载
* 支持灵活的模板表达式
* 支持拦截器处理请求的各个生命周期
* 支持自定义注解
* 支持OAuth2验证
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
    <artifactId>forest-spring-boot-starter</artifactId>
    <version>1.5.2-BETA</version>
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
    @Get("http://ditu.amap.com/service/regeo?longitude=${0}&latitude=${1}")
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

## 发送JSON数据

```java
/**
 * 将对象参数解析为JSON字符串，并放在请求的Body进行传输
 */
@Post("/register")
String registerUser(@JSONBody MyUser user);

/**
 * 将Map类型参数解析为JSON字符串，并放在请求的Body进行传输
 */
@Post("/test/json")
String postJsonMap(@JSONBody Map mapObj);

/**
 * 直接传入一个JSON字符串，并放在请求的Body进行传输
 */
@Post("/test/json")
String postJsonText(@JSONBody String jsonText);
```

## 发送XML数据

```java
/**
 * 将一个通过JAXB注解修饰过的类型对象解析为XML字符串
 * 并放在请求的Body进行传输
 */
@Post("/message")
String sendXmlMessage(@XMLBody MyMessage message);

/**
 * 直接传入一个XML字符串，并放在请求的Body进行传输
 */
@Post("/test/xml")
String postXmlBodyString(@XMLBody String xml);
```

## 文件上传

```java
/**
 * 用@DataFile注解修饰要上传的参数对象
 * OnProgress参数为监听上传进度的回调函数
 */
@Post("/upload")
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

## 多文件批量上传

```java
/**
 * 上传Map包装的文件列表，其中 ${_key} 代表Map中每一次迭代中的键值
 */
@Post("/upload")
ForestRequest<Map> uploadByteArrayMap(@DataFile(value = "file", fileName = "${_key}") Map<String, byte[]> byteArrayMap);

/**
 * 上传List包装的文件列表，其中 ${_index} 代表每次迭代List的循环计数（从零开始计）
 */
@Post("/upload")
ForestRequest<Map> uploadByteArrayList(@DataFile(value = "file", fileName = "test-img-${_index}.jpg") List<byte[]> byteArrayList);
```

## 文件下载

下载文件也是同样的简单

```java
/**
 * 在方法上加上@DownloadFile注解
 * dir属性表示文件下载到哪个目录
 * OnProgress参数为监听上传进度的回调函数
 * ${0}代表引用第一个参数
 */
@Get("http://localhost:8080/images/xxx.jpg")
@DownloadFile(dir = "${0}")
File downloadFile(String dir, OnProgress onProgress);
```


调用下载接口以及监听下载进度的代码如下：

```java
File file = myClient.downloadFile("D:\\TestDownload", progress -> {
    System.out.println("progress: " + Math.round(progress.getRate() * 100) + "%");  // 已下载百分比
    if (progress.isDone()) {   // 是否下载完成
        System.out.println("--------   Download Completed!   --------");
    }
});
```

## 基本签名验证

```java
@Post("/hello/user?username=${username}")
@BasicAuth(username = "${username}", password = "bar")
String send(@DataVariable("username") String username);
```

## OAuth 2.0

```java
@OAuth2(
        tokenUri = "/auth/oauth/token",
        clientId = "password",
        clientSecret = "xxxxx-yyyyy-zzzzz",
        grantType = OAuth2.GrantType.PASSWORD,
        scope = "any",
        username = "root",
        password = "xxxxxx"
)
@Get("/test/data")
String getData();
```

## 自定义注解

Forest允许您根据需要自行定义注解，不但让您可以简单优雅得解决各种需求，而且极大得扩展了Forest的能力。

### 定义一个注解

```java
/**
 * 用Forest自定义注解实现一个自定义的签名加密注解
 * 凡用此接口修饰的方法或接口，其对应的所有请求都会执行自定义的签名加密过程
 * 而自定义的签名加密过程，由这里的@MethodLifeCycle注解指定的生命周期类进行处理
 * 可以将此注解用在接口类和方法上
 */
@Documented
/** 重点： @MethodLifeCycle注解指定该注解的生命周期类*/
@MethodLifeCycle(MyAuthLifeCycle.class)
@RequestAttributes
@Retention(RetentionPolicy.RUNTIME)
/** 指定该注解可用于类上或方法上 */
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface MyAuth {

    /** 
     * 自定义注解的属性：用户名
     * 所有自定注解的属性可以在生命周期类中被获取到
     */
    String username();

    /** 
     * 自定义注解的属性：密码
     * 所有自定注解的属性可以在生命周期类中被获取到
     */
    String password();
}
```

### 定义注解生命周期类

```java
/**
 *  MyAuthLifeCycle 为自定义的 @MyAuth 注解的生命周期类
 * 因为 @MyAuth 是针对每个请求方法的，所以它实现自 MethodAnnotationLifeCycle 接口
 * MethodAnnotationLifeCycle 接口带有泛型参数
 * 第一个泛型参数是该生命周期类绑定的注解类型
 * 第二个泛型参数为请求方法返回的数据类型，为了尽可能适应多的不同方法的返回类型，这里使用 Object
 */
public class MyAuthLifeCycle implements MethodAnnotationLifeCycle<MyAuth, Object> {

 
    /**
     * 当方法调用时调用此方法，此时还没有执行请求发送
     * 次方法可以获得请求对应的方法调用信息，以及动态传入的方法调用参数列表
     */
    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        System.out.println("Invoke Method '" + method.getMethodName() + "' Arguments: " + args);
    }

    /**
     * 发送请求前执行此方法，同拦截器中的一样
     */
    @Override
    public boolean beforeExecute(ForestRequest request) {
        // 通过getAttribute方法获取自定义注解中的属性值
        // getAttribute第一个参数为request对象，第二个参数为自定义注解中的属性名
        String username = (String) getAttribute(request, "username");
        String password = (String) getAttribute(request, "password");
        // 使用Base64进行加密
        String basic = "MyAuth " + Base64Utils.encode("{" + username + ":" + password + "}");
        // 调用addHeader方法将加密结构加到请求头MyAuthorization中
        request.addHeader("MyAuthorization", basic);
        return true;
    }

    /**
     * 此方法在请求方法初始化的时候被调用
     */
    @Override
    public void onMethodInitialized(ForestMethod method, BasicAuth annotation) {
        System.out.println("Method '" + method.getMethodName() + "' Initialized, Arguments: " + args);
    }
}
```

### 使用自定义的注解

```java
/**
 * 在请求接口上加上自定义的 @MyAuth 注解
 * 注解的参数可以是字符串模板，通过方法调用的时候动态传入
 * 也可以是写死的字符串
 */
@Get("/hello/user?username=${username}")
@MyAuth(username = "${username}", password = "bar")
String send(@DataVariable("username") String username);
```

## 详细文档请看：[http://forest.dtflyx.com/](http://forest.dtflyx.com/)

## 贡献者列表
[![Giteye chart](https://chart.giteye.net/gitee/dromara/forest/2E26F7V4.png)](https://giteye.net/chart/2E26F7V4)
正因为有他们的贡献，Forest才得以走下去

## 联系作者

亲，进群前记得先star一下哦~

扫描二维码关注公众号，点击菜单中的 `进群` 按钮即可进群

![avatar](data:image/jpeg;base64,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)


## Gitee上的Star趋势图
[![Giteye chart](https://chart.giteye.net/gitee/dromara/forest/NURRL346.png)](https://giteye.net/chart/NURRL346)



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


