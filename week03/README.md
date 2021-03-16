## 要求：
### 需求一（必须）

- 整合 https://jolokia.org/
  实现一个自定义 JMX MBean，通过 Jolokia 做 Servlet 代理

### 需求二（选做）

- 继续完成 Microprofile config API 中的实现
  - 扩展 org.eclipse.microprofile.config.spi.ConfigSource 实现，包括 OS 环境变量，以及本地配置文件
  - 扩展 org.eclipse.microprofile.config.spi.Converter 实现，提供 String 类型到简单类型
- 通过 org.eclipse.microprofile.config.Config 读取当前应用名称
  - 应用名称 property name = “application.name”

