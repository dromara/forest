# Forest - HTTP客户端访问框架


[![Build Status](https://api.travis-ci.org/mySingleLive/forest.svg?branch=master)](https://travis-ci.org/mySingleLive/forest)
[![license](https://img.shields.io/badge/license-MIT%20License-blue.svg)](https://opensource.org/licenses/mit-license.php)

项目介绍：
-------------------------------------

Forest是一个高层的、极简的HTTP调用API框架。<br>
相比于直接使用Httpclient您不再用写一大堆重复的代码了，而是像调用本地方法一样去发送HTTP请求。

项目特点：
-----
* 以Httpclient和OkHttp为后端框架
* 通过调用本地方法的方式去发送Http请求
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


项目状态：
-------------------------------------

* 主流程已完成
* 已支持所有请求方法：GET, HEAD, OPTIONS, TRACE, POST, DELETE, PUT, PATCH
* 已支持的JSON转换框架: Fastjson, Jackson, Gson
* 已支持Spring集成
* 已支持JAXB形式的XML转换
* 已支持JSON解析
* 支持Commons-Log, SLF4j, Log4J等日志框架
* 异步请求方式还不够完善
* 暂未发布中央仓库


Quick Start
-------------------------------------


### Maven依赖

```xml
<dependency>
	<groupId>com.dtflys</groupId>
	<artifactId>forest</artifactId>
	<version>1.0.0</version>
</dependency>
```

一个栗子：访问百度短链接REST接口

### 创建一个Interface作为远程调用接口


```java

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


### 调用远程接口
```java

ForestConfiguration configuration = ForestConfiguration.configuration();
MyClient myClient = configuration.createInstance(MyClient.class);
Map result = myClient.getShortUrl("https://gitee.com/dt_flys/forest");
System.out.println(result);

```

#### 或者在Spring中调用

```java
@Autowired
MyClient myClient;

...

Map result = myClient.getShortUrl("https://gitee.com/dt_flys/forest");

```

如何在Spring中配置请参见[在Spring中使用](https://gitee.com/dt_flys/forest/blob/master/forest-core/src/main/doc/SPRING.md)

### 详细文档:<br>
* [变量/参数](https://gitee.com/dt_flys/forest/blob/master/forest-core/src/main/doc/DOCUMENTATION.md)<br>
* [回调方法](https://gitee.com/dt_flys/forest/blob/master/forest-core/src/main/doc/CALLBACK.md)<br>
* [在Spring中使用](https://gitee.com/dt_flys/forest/blob/master/forest-core/src/main/doc/SPRING.md)


项目协议
--------------------------
The MIT License (MIT)

Copyright (c) 2016 Jun Gong