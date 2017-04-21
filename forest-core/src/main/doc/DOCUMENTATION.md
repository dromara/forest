# Forest 使用文档

### 参数/变量引用

方法一：使用${数字}的方式引用对应顺序的参数

```java

@Request(
    url = "${0}/send?un=${1}&pw=${2}&da=${3}&sm=${4}",
    type = "get",
    dataType = "json"
)
public Map send(
    String base,
    String userName,
    String password,
    String phoneList,
    String content
);



client.send("http://localhost:8080", "DT", "123456", "123888888", "Hahaha");
// 最终会产生以下链接并访问
// http://localhost:8080/send?un=DT&pw=123456&da=123888888&sm=Hahaha

```


方法二：使用@DataParam标签, 自动产生url参数

```java

@Request(
    url = "${0}/send",
    type = "get",
    dataType = "json"
)
public Map send(
    String base,
    @DataParam("un") String userName,
    @DataParam("pw") String password,
    @DataParam("da") String phoneList,
    @DataParam("sm") String content
);

client.send("http://localhost:8080", "DT", "123456", "123888888", "Hahaha");
// 效果同上，最终会产生以下链接并访问
// http://localhost:8080/send?un=DT&pw=123456&da=123888888&sm=Hahaha


```


方法三：使用@DataVariable标签定义变量名，然后引用通过${变量名}的方式引用

```java

@Request(
    url = "${base}/send?un=${un}&pw=${pw}&da=${da}&sm=${sm}",
    type = "get",
    dataType = "json"
)
public Map send(
    @DataVariable("base") String base,
    @DataVariable("un") String userName,
    @DataVariable("pw") String password,
    @DataVariable("da") String phoneList,
    @DataVariable("sm") String content
);

client.send("http://localhost:8080", "DT", "123456", "123888888", "Hahaha");
// 效果同上，最终会产生以下链接并访问
// http://localhost:8080/send?un=DT&pw=123456&da=123888888&sm=Hahaha


```

方法四：设置全局变量，然后引用通过${变量名}的方式引用

```java

@Request(
        url = "${baseShortUrl}/create.php",
        type = "post",
        dataType = "json"
)
Map testVar(@DataParam("url") String url);



ForestConfiguration configuration = ForestConfiguration.configuration();
configuration.setVariableValue("baseShortUrl", "http://dwz.cn");
Www www = configuration.createInstance(Www.class);
Map result = www.testVar("https://github.com/mySingleLive");

```