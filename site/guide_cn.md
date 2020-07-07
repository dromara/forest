
# 一. 新手介绍

## 什么是Forest？

Forest是一个开源的Java HTTP客户端框架，它能够将HTTP的所有请求信息（包括URL、Header以及Body等信息）绑定到您自定义的Interface方法上，能够通过调用本地接口方法的方式发送HTTP请求。

## 为什么使用Forest?

使用Forest就像使用类似Dubbo那样的RPC框架一样，只需要定义接口，调用接口即可，不必关心具体发送HTTP请求的细节。同时将HTTP请求信息与业务代码解耦，方便您统一管理大量HTTP的URL、Header等信息。而请求的调用方完全不必在意HTTP的具体内容，即使该HTTP请求信息发生变更，大多数情况也不需要修改调用发送请求的代码。

## Forest的架构

![avater](media/architect.png)

Forest分为前端和后端部分，Forest本身是前端框架，是对后端HTTP Api框架的进一步封装。

## 对应的Java版本

Forest 1.0.x和Forest 1.1.x基于JDK 1.7, Forest 1.2.x基于JDK 1.8

# 二. 安装

## 2.1 在SpringBoot项目中安装

若您的项目已经依赖了spring boot，那只要添加下面一个maven依赖便可。

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

## 3.1 在SpringBoot项目中配置

在application.yml中设置forest.enabled为true，便能开启forest。若设为false，springboot便不会再扫描forest。

```yaml
forest:
  enabled: true
```

配置后端HTTP API

```yaml
forest:
  enabled: true       
  backend: okhttp3         # 配置后端HTTP API为 okhttp3
```

目前Forest支持okhttp3和httpclient两种后端HTTP API，若不配置该属性，默认为okhttp3.
当然，您也可以改为httpclient

```yaml
forest:
  enabled: true       
  backend: httpclient         # 配置后端HTTP API为 httpclient
```

Forest运行您在yaml文件中配置bean id，它对应着ForestConfiguration对象在spring上下文中的bean名称，可以在spring中通过bean的名称引用到它。

```yaml
forest:
  enabled: true
  bean-id: config0
```

```java
@Resource(name = "config0")
private ForestConfiguration config0;
```

其他的基本配置

```yaml
forest:
  enabled: true                           # forest开关
  bean-id: config0                        # 在spring上下文中bean的id
  backend: okhttp3                        # 后端HTTP API： okhttp3
  max-connections: 3000                   # 连接池最大连接数
  max-route-connections: 3000             # 每个路由的最大连接数
  timeout: 3000                           # 请求超时时间
  connect-timeout: 3000                   # 连接超时时间
  retry-count: 1                          # 请求失败后重试次数
  ssl-protocol: SSLv3                     # 单向验证的HTTPS的默认SSL协议
```

## 3.1 在普通项目中配置

