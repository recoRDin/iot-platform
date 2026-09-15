# IoT Learning

参照本地 BladeX 的组件职责组织的 Spring Boot 3.2.10 / Java 17 学习工程。
底层组件为本项目自行实现，目前只包含统一返回对象、结果码、业务异常和 MVC 业务异常处理。

## 目录与职责

```text
iot-learning
├── pom.xml                          根聚合工程与版本管理
├── iot-core
│   ├── pom.xml                      公共组件聚合工程
│   ├── iot-core-tool                R、结果码、基础工具
│   └── iot-starter-log              业务异常、异常转换与自动配置
└── iot-service
    ├── pom.xml                      服务聚合工程
    └── iot-server                   业务服务及唯一启动入口
```

`iot-server` 直接依赖 `iot-core-tool` 和 `iot-starter-log`，后者也依赖 `iot-core-tool`。
三个聚合工程使用 `packaging=pom`，两个公共组件使用普通 JAR，只有 `iot-server` 打包为可运行的 Spring Boot JAR。

## 代码位置

- 基础返回类：`com.iot.core.tool.api`。
- 业务异常：`com.iot.core.log.exception.ServiceException`。
- 异常转换器：`com.iot.core.log.error.RestExceptionTranslator`。
- 自动配置：`com.iot.core.log.config.IotLogAutoConfiguration`。
- 服务入口：`com.iot.server.IotServerApplication`。
- 产品业务预留包：`com.iot.server.iot.product`，内部按 `controller`、`service`、`service.impl` 分层。

Starter 通过 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 注册自动配置。
服务引用依赖即可使用，无须扩大组件扫描范围或在启动类上手写 `@Import`。
自动配置只在 Servlet Web 应用中注册异常转换器；应用提供同类型转换器时，默认实现会退让。
注册机制参见 [Spring Boot 3.2.10 自动配置文档](https://docs.spring.io/spring-boot/docs/3.2.10/reference/html/features.html#features.developing-auto-configuration)。

## 构建与运行

先确认 Maven 使用 JDK 17（`mvn -version`），在项目根目录执行：

```shell
mvn verify
java -jar iot-service/iot-server/target/iot-server-1.0-SNAPSHOT.jar
```

也可在 IDEA 中重新加载根 `pom.xml` 后运行 `IotServerApplication`。
当前没有正式业务接口，直接访问 `/` 出现 404 是正常情况。

## 异常处理范围与验证

MVC 请求中抛出的 `ServiceException` 会返回 HTTP 400，响应 JSON 的 `code` 保留业务结果码，`msg` 保留异常提示，`data` 为 null。
服务端使用 SLF4J 记录异常及堆栈。参数校验、其他系统异常、MQTT 和后台任务异常尚未在本组件中统一处理。

`ExceptionHandlingIntegrationTest` 验证 starter 自动发现、成功响应、自定义提示、默认提示以及业务码与 HTTP 状态分离。
`LogAutoConfigurationTest` 验证非 Web 场景不注册和用户自定义处理器优先。
测试接口只存在于 `src/test/java`，不会打入正式服务 JAR。

