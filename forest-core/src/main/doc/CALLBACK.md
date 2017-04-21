# Forest 使用文档

### 回调方法

OnSuccess - 请求成功:

```java

@Request(
    url = "http://dwz.cn/create.php",
    type = "post",
    dataType = "json"
)
String testOnSuccess(@DataParam("url") String url, OnSuccess<Map> onSuccess);

...

client.testOnSuccess("https://github.com/mySingleLive", new OnSuccess<Map>() {
    public void onSuccess(Map data, ForestRequest request, ForestResponse response) {
        System.out.println(data);
        assertNotNull(data);
        assertEquals(data.get("status"), 0);
    }
});


```


OnError - 请求失败:


```java

@Request(url = "http://this_is_a_error_address", timeout = 10)
String testError(OnError onError);

...

client.testError(new OnError() {
    public void onError(ForestRuntimeException ex, ForestRequest request) {
        assertNotNull(ex);
        assertNotNull(request);
    }
});


```

