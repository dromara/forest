# Forest 使用文档

### 在Spring中使用


Maven依赖

```xml

    <dependency>
        <groupId>org.apache.httpcomponents</groupId>
        <artifactId>httpclient-cache</artifactId>
        <version>4.1.2</version>
    </dependency>


    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-beans</artifactId>
        <version>${spring.version}</version>
    </dependency>

    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>${spring.version}</version>
    </dependency>

    <dependency>
        <groupId>org.forest</groupId>
        <artifactId>forest-core</artifactId>
        <version>0.0.6</version>
    </dependency>

    <dependency>
        <groupId>org.forest</groupId>
        <artifactId>forest-spring</artifactId>
        <version>0.0.6</version>
    </dependency>

```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:forest="http://forest.org/schema/forest-spring" xmlns:util="http://www.springframework.org/schema/util"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://forest.org/schema/forest-spring
       http://forest.org/schema/forest-spring.xsd http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util.xsd">

    <forest:configuration
            id="forestConfiguration"
            timeout="30000"
            retryCount="3"
            connectTimeout="10000"
            maxConnections="500"
            maxRouteConnections="500">

        <forest:var name="baseUrl" value="http://www.thebeastshop.com"/>
        <forest:var name="x" value="0"/>
        <forest:var name="y" value="1"/>

    </forest:configuration>

    <forest:scan configuration="forestConfiguration"
                 base-package="org.forest.spring.client"/>

</beans>
```
