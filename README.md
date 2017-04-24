# HTTP请求访问框架 Forest

项目介绍：
-------------------------------------

Forest是一个通过动态代理模式实现的HTTP访问接口的框架。<br>
相比直接使用Httpclient您不再用写一大堆重复的代码了，只需像调用本地方法一样调用HTTP链接。

项目状态：
-------------------------------------

* 主流程已完成
* 已支持GET, HEAD, OPTIONS, TRACE, POST, DELETE请求方法
* 已支持Spring集成（仅配置定义）
* 现只支持文本和JSON两种返回解析方式
* 支持Commons-Log, SLF4j, Log4J等日志框架
* 暂不支持异步请求方式 (计划中)


如何获取：
-------------------------------------

### Maven配置

设置maven仓库地址

```xml

    <repository>
        <id>mySingleLive-maven-repo</id>
        <url>https://raw.githubusercontent.com/mySingleLive/maven-repo/master/repository</url>
    </repository>
```

依赖配置

```xml

    <dependency>
        <groupId>org.apache.httpcomponents</groupId>
        <artifactId>httpclient-cache</artifactId>
        <version>4.1.2</version>
    </dependency>

    <dependency>
        <groupId>org.forest</groupId>
        <artifactId>forest-core</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>

```


使用方法：
-------------------------------------
### 创建一个Interface作为远程调用接口

```java

import org.forest.annotation.Request;
import org.forest.annotation.var.DataParam;

public interface Www {

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
ProxyFactory<Www> factory = configuration.getProxyFactory(Www.class);
Www www = factory.createInstance();
Map result = www.getShortUrl("https://github.com/mySingleLive");
System.out.println(result);

```


详细文档:<br>
* [变量/参数](https://github.com/mySingleLive/forest/blob/master/forest-core/src/main/doc/DOCUMENTATION.md)<br>
* [回调方法](https://github.com/mySingleLive/forest/blob/master/forest-core/src/main/doc/CALLBACK.md)<br>
* [在Spring中使用](https://github.com/mySingleLive/forest/blob/master/forest-core/src/main/doc/SPRING.md)
