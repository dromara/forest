
Spring Boot 环境下接入 ChartGPT 的示例
====

本案例使用`forest-spring-boot-starter`包发送HTTP请求来调用 OpenAI 的接口

使用 Fastjson 进行JSON序列化/反序列化

#### 项目结构介绍

本案例包含以下几个类:

- ChartGPTExampleApplication: 项目启动类
- ChartGPT: 接入 ChartGPT 的 Forest 声明式接口
- GPTResponse: 用于接受 ChartGPT 响应结果的数据类
- GPTChoice: 用于接受 ChartGPT 响应结果中文本内容的数据类

#### 配置

```yaml
forest:
  connect-timeout: 60000      # HTTP请求连接超时时间
  read-timeout: 60000         # HTTP请求读取超时时间
  variables:                  # 自定义变量:
    apiKey: YOUR_API_KEY      # 你的 OpenAI 的 API KEY
    model: text-davinci-003   # ChartGPT 的模型
    maxTokens: 50             # 最大 Token 数
    temperature: 0.5          # 该值越大每次返回的结果越随机，即相似度越小
```


