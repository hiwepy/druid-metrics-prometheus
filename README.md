# druid-metrics-prometheus
Druid Metrics For Prometheus

### 组件简介

> Druid + Actuator 的 Spring Boot Starter 实现；主要代码来自 ：
> https://gitee.com/596392912/mica/tree/master/mica-metrics

### 使用说明

##### 1、Spring Boot 项目添加 Maven 依赖

``` xml
<dependency>
	<groupId>com.github.hiwepy</groupId>
	<artifactId>druid-metrics-prometheus</artifactId>
	<version>${project.version}</version>
</dependency>
```

##### 2、使用示例

访问本地配置：

http://localhost:8080/actuator/prometheus

```
# Connection connect max time
druid_connections_connect_max_time{application="druid-app",} 0.0
# Connection alive max time
druid_connections_alive_max_time{application="druid-app",} 0.0
# Connection alive min time
druid_connections_alive_min_time{application="druid-app",} 0.0
# Connection connect count
druid_connections_connect_count{application="druid-app",} 0.0
# Connection active count
druid_connections_active_count{application="druid-app",} 0.0
# Connection close count
druid_connections_close_count{application="druid-app",} 0.0
# Connection error count
druid_connections_error_count{application="druid-app",} 0.0
# Connection connect error count
druid_connections_connect_error_count{application="druid-app",} 0.0
# Connecting commit count
druid_connections_commit_count{application="druid-app",} 0.0
# Connection rollback count
druid_connections_rollback_count{application="druid-app",} 0.0
```

##### 3、Prometheus 集成


##### 4、Grafana 集成


## Jeebiz 技术社区

Jeebiz 技术社区 **微信公共号**、**小程序**，欢迎关注反馈意见和一起交流，关注公众号回复「Jeebiz」拉你入群。

|公共号|小程序|
|---|---|
| ![](https://raw.githubusercontent.com/hiwepy/static/main/images/qrcode_for_gh_1d965ea2dfd1_344.jpg)| ![](https://raw.githubusercontent.com/hiwepy/static/main/images/gh_09d7d00da63e_344.jpg)|
