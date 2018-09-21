# Forest 使用文档

### HTTP请求

简单的GET请求只要定义好url和dataType，type默认为"get"所以可以不填。<br>
这里的dataType指定了response返回的数据类型，可以是json或是xml，forest会根据数据类型自动解析成对应类型的对象。

```java
// 最简单的形式：既没有参数，也没有请求头
@Request(
    url = "http://localhost:8080/hello",
    dataType = "json"
)
MyResp getHello();
```

其它类型请求有包括 POST, PUT, HEAD 等，都可通过@Request注解的type属性设置。

```java
@Request(
    url = "http://localhost:8080/hello",
    type = "post",   // 此处类型值为字符串，大小写不敏感
    dataType = "json"
)
MyResp postHello();


@Request(
    url = "http://localhost:8080/hello",
    type = "put",
    dataType = "json"
)
MyResp putHello();


@Request(
    url = "http://localhost:8080/hello",
    type = "head",
    dataType = "json"
)
MyResp headHello();


```


### @DataParam

Forest定义参数的方法有很多，但最常用的还是使用@DataParam注解和参数下标。<br>
@DataParam注解只需要定义好参数名即可，随后在调用该方法时，会自动把传入的参数值绑定到request参数上。<br>
@DataParam注解的定义无需关心request类型是GET还是POST还是其它，其定义的形式都是不变的。<br>
在GET类型的request中，@DataParam绑定的参数值绑定到URL的Query部分（也就是?后面那串东西），<br>
而在POST或PUT类型的request中，参数会被绑定到HTTP请求的Body部分中。

```java
@Request(
    url = "http://localhost:8080/send",
    dataType = "json"
)
public MyResp send(
    @DataParam("un") String userName,
    @DataParam("pw") String password,
    @DataParam("da") String phoneList,
    @DataParam("sm") String content
);
```

### 参数下标

大多数情况下@DataParam都可以很好的满足要求，但有时并不想定义又长有多的标注。<br>
这时候就可以用参数下标，通过字符串模板表达式+数字的形式进行绑定对应的参数。

格式： ${数字}

```java
@Request(
    url = "http://localhost:8080/send?un=${1}&pw=${2}&da=${3}&sm=${4}",
    retryCount = 3,     // 如果失败，最多重试3次
    dataType = "json"
)
MyResp send(
    String userName,
    String password,
    String phoneList,
    String content
);
```

除了@DataParam注解和参数下标的方式之外，还有变量绑定数据的办法。关于变量和参数的详细内容请看[这里](DOCUMENTATION.md)


### Headers

Forest支持在请求中定义请求头，其通过@Request注解中headers属性进行设置。<br>
请求头设置的格式为："key: value"字符串形式，key和value以冒号分割。

```java
@Request(
    url = "http://localhost:8080/send",
    headers = "Content-Type: application/x-www-form-urlencoded",
    dataType = "json"
)
MyResp send(
    @DataParam("un") String userName,
    @DataParam("pw") String password,
    @DataParam("da") String phoneList,
    @DataParam("sm") String content
);
```

请求头的设置一样支持变量和参数

```java
@Request(
    url = "http://localhost:8080/send",
    headers = "Content-Type: ${contentType}",
    dataType = "json"
)
MyResp send(
    @DataParam("un") String userName,
    @DataParam("pw") String password,
    @DataParam("da") String phoneList,
    @DataParam("sm") String content,
    @DataVariable("contentType") String contentType
);
```

既然headers是复数，那肯定是支持多header的。

```java
@Request(
    url = "http://localhost:8080/send",
    headers = {
            "Content-Type: ${4}",
            "Accept: ${5}"
    },
    dataType = "json"
)
MyResp send(
    @DataParam("un") String userName,
    @DataParam("pw") String password,
    @DataParam("da") String phoneList,
    @DataParam("sm") String content,
    String contentType,
    String accept
);
```


### 重试

Forest在默认情况下，一旦网络访问失败（如碰到404,500）变回抛出异常，并不会进行重试。
但只要设置好重试次数，forest便会按设置的次数进行重试发送请求。
若重复发送请求的次数达到设置好的次数后依然访问失败，便会依旧抛出异常。

```java
@Request(
    url = "http://localhost:8080/send?un=${1}&pw=${2}&da=${3}&sm=${4}",
    retryCount = 3,     // 如果失败，最多重试3次
    dataType = "json"
)
MyResp send(String un, String pw, String da, String sm);
```


### @BaseURL和@BaseRequest

当定义了很多Request请求方法后，自然就会有很多重复的信息。抽象代码、提高代码重用性就变成必不可少的工作。<br>
Forest提供了@BaseURL和@BaseRequest来帮助您定义公用的URL和重复请求信息。


```java

@BaseURL("http://localhost:8080")
@BaseRequest(timeout = 3000, headers = "Accept:text/plan", retryCount = 0)
public interface Sender {
    
    @Request(
        url = "/send?un=${0}&pw=${1}&da=${2}&sm=${3}",
        dataType = "json"
    )
    MyResp send(String un, String pw, String da, String sm);
        
    @Request(
        url = "/receive?un=${0}",
        dataType = "json"
    )
    MyResp receive(String un);
}

```

@BaseURL定义了基本域名和端口号或是根路径，而在该interface以下定义的@Request的url就不需要填写url前半部分了。

@BaseRequest则定义了公用的请求信息，如超时时间、请求头、重试次数等等，其interface下定义的@Request可以不再用设置这些信息了，会自动填写@BaseRequest中的对应属性值。<br>
但在具体的@Request一样还是可以设置这些属性的，其效果便是会覆盖掉@BaseRequest设置对应内容。