# Forest 使用文档

### 拦截器

用过Spring MVC的朋友一定对spring的拦截器并不陌生，Forest也同样支持针对Forest请求的拦截器。

如果你想在很多个请求发送之前或之后做一些事情（如下日志、计数等等），拦截器就能帮你做到这些事情。

#### 拦截器定义

定义一个拦截器需要实现com.dtflys.forest.interceptor接口

````java

public class SimpleInterceptor implements Interceptor<String> {

    private final static Logger log = LoggerFactory.getLogger(SimpleInterceptor.class);

    /**
     * 该方法在请求发送之前被调用, 若返回false则不会继续发送请求
     */
    @Override
    public boolean beforeExecute(ForestRequest request) {
        log.info("invoke Simple beforeExecute");
        return true;
    }

    /**
     * 该方法在请求成功响应时被调用
     */
    @Override
    public void onSuccess(String data, ForestRequest request, ForestResponse response) {
        log.info("invoke Simple onSuccess");
    }

    /**
     * 该方法在请求发送失败时被调用
     */
    @Override
    public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {
        log.info("invoke Simple onError");
    }

    /**
     * 该方法在请求发送之后被调用
     */
    @Override
    public void afterExecute(ForestRequest request, ForestResponse response) {
        log.info("invoke Simple afterExecute");
    }
}

````
Interceptor接口带有一个泛型参数，其表示的是请求响应后返回的数据类型。
Interceptor<String>即代表返回的数据类型为String。

#### 调用拦截器

需要调用拦截器的地方，只需要在该方法的@Request注解中设置interceptor属性即可。

```java

public interface SimpleClient {

    @Request(
            url = "http://localhost:8080/hello/user?username=foo",
            headers = {"Accept:text/plain"},
            interceptor = SimpleInterceptor.class
    )
    String simple();
}
```
