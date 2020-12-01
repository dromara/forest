# Forest 使用文档

### 在Spring中使用

 * 添加依赖

Maven中除了forest-core和spring外，还要加入以下依赖

```xml

    <dependency>
        <groupId>com.dtflys.forest</groupId>
        <artifactId>forest-spring</artifactId>
        <version>1.5.0-BETA9</version>
    </dependency>

```

 * 打开spring的上下文配置文件，在beans开头定义的属性中加入Forest的Schema

```
xmlns:forest="http://www.dtflys.com/schema/forest" xmlns:util="http://www.springframework.org/schema/util"
   ...
xsi:schemaLocation=" ...
http://www.dtflys.com/schema/forest
http://www.dtflys.com/schema/forest/forest-spring.xsd http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util.xsd
..."
```

加入完成后效果如下

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:forest="http://www.dtflys.com/schema/forest"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.dtflys.com/schema/forest
       http://www.dtflys.com/schema/forest/forest-spring.xsd">

   ...

</beans>
```

 * 添加Forest基本配置的定义

```xml
   <forest:configuration
           id="forestConfiguration"
           timeout="30000"
           retryCount="3"
           connectTimeout="10000"
           maxConnections="500"
           maxRouteConnections="500">

       <!-- forest变量定义 开始 -->
       <forest:var name="baseUrl" value="http://www.xxx.com"/>
       <forest:var name="x" value="0"/>
       <forest:var name="y" value="1"/>
       <!-- forest变量定义 结束 -->

   </forest:configuration>
```
 1. 使用forest:configuration标签创建在Spring中的ForestConfiguration Bean
 2. 使用forest:var标签定义变量

    注意：***变量的作用域为该ForestConfiguration之下，所有跟这个配置对象绑定的Client都能访问到其下的变量，而别的ForestConfiguration下定义的变量不能访问。***

 * 创建Client Bean有两种方式

 1. 通过forest:client标签创建单个Client Bean

```xml
   <forest:client id="siteAClient" configuration="forestConfiguration" class="com.xxx.client.SiteAClient"/>
```

 2. 通过forest:scan标签制定back-package的方式批量创建Client Bean

```xml
    <forest:scan configuration="forestConfiguration" base-package="com.xxx.client"/>
```

 * 在其他的Class中注入Client

```java
   ...

   @Autowired
   private SiteAClient siteAClient;

   ...

```
