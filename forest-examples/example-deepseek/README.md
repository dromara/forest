
Spring Boot 环境下接入 DeepSeek 的示例
====

本案例使用`forest-spring-boot-starter`包发送HTTP请求来调用 DeepeSeek 的接口

使用 Fastjson 进行JSON序列化/反序列化

#### 项目结构介绍

本案例包含以下几个类:

- DeepSeekExampleApplication: 项目启动类
- DeepSeek: 接入 DeepSeek 的 Forest 声明式接口
- DeepSeekResult: 用于接受 DeepSeekResult 响应结果的数据类
- GPTChoice: 用于接受 ChartGPT 响应结果中文本内容的数据类

#### 配置

```yaml
forest:
  connect-timeout: 10000      # HTTP请求连接超时时间
  read-timeout: 3600000       # HTTP请求读取超时时间
  variables:                  # 自定义变量:
    baseUrl: https://api.deepseek.com
    apiKey: YOUR_API_KEY      # 你的 DeepSeek 的 API KEY
    model: deepseek-reasoner  # DeepSeek 模型: deepseek-chat 或 deepseek-reasoner
```


