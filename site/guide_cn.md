
# 一. 新手介绍

## 什么是Forest？

Forest是一个开源的Java HTTP客户端框架，它能够将HTTP的所有请求信息（包括URL、Header以及Body等信息）绑定到您自定义的Interface方法上，能够通过调用本地接口方法的方式发送HTTP请求。

## 为什么使用Forest?

使用Forest就像使用类似Dubbo那样的RPC框架一样，只需要定义接口，调用接口即可，不必关心具体发送HTTP请求的细节。同时将HTTP请求信息与业务代码解耦，方便您统一管理大量HTTP的URL、Header等信息。而请求的调用方完全不必在意HTTP的具体内容，即使该HTTP请求信息发生变更，大多数情况也不需要修改调用发送请求的代码。

## 对应的Java版本

Forest 1.1.x基于JDK1.7, Forest 1.2.x基于JDK 1.8

# 二. 安装

## 2.1 在SpringBoot项目中安装

### 依赖
```xml
<dependency>
    <groupId>com.dtflys.forest</groupId>
    <artifactId>spring-boot-starter-forest</artifactId>
    <version>1.2.0</version>
</dependency>
```
最新版本为<font color=red>*1.2.0*</font>，为稳定版本


## 2.2 在普通项目中安装

### 依赖

先添加后端HTTP API的依赖：okhttp3 或 httpclient 4.3.x.
以及JSON解析框架：jackson、fastjson或Gson
```xml
<dependency>
  <groupId>com.squareup.okhttp3</groupId>
  <artifactId>okhttp</artifactId>
  <version>3.3.0</version>
</dependency>

<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.47</version>
</dependency>
```

然后添加forest核心包依赖

```xml
<dependency>
    <groupId>com.dtflys.forest</groupId>
    <artifactId>forest-core</artifactId>
    <version>1.2.0</version>
</dependency>
```

# 三. 配置

