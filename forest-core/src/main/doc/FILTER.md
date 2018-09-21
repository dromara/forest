# Forest 使用文档

### 过滤器

##### 一. 过滤器的用处

当一个数据通过参数转换为HTTP请求的过程中可能需要很多个环节进行处理，
如传入一个User对象，你需要把它转换为JSON字符串，并进行SHA加密，再放入一个大JSON里，最后才放入Request Body中。
那其中就两个过滤器：分别为 JSON 和 SHA过滤器。

处理顺序为: User对象 > JSON > SHA

##### 二. 使用方法

还是以User对象为例子，有如下代码：

* 2.1 不用过滤器的情况

```java

import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.annotation.DataParam;

public interface MyClient {
    
    @Request(
            url = "http://xxx.com/login",
            type = "post",
            data = "${0}"
    )
    Result loginUser(@DataParam("user") String str);
    
}

```

##### 1. 内置过滤器



##### 