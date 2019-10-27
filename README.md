# SpringBoot-Mysql模板多数据源加载

[![Maven Central](https://img.shields.io/maven-central/v/com.purgeteam/mysql-datasource-spring-boot-starter.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:com.purgeteam%20AND%20a:mysql-datasource-spring-boot-starter)
![License](https://img.shields.io/badge/SpringBoot-2.1.8.RELEASE-green.svg)
![License](https://img.shields.io/badge/JAVA-1.8+-green.svg)
![License](https://img.shields.io/badge/maven-3.0+-green.svg)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)


> qq交流群: `812321371`
> 微信交流群: `MercyYao`

## 简介

在 java 项目里常用到 mysql 多数据源操作。结合 springboot 使用原有的还是很方便的。
不过需要配置多套数据源的配置。

在微服务里, 数据库连接之类的配置是单独拆开读取。相当于一个模板。

如下mysql:

```
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/${config.mysql.name}?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&autoReconnect=true
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
spring.datasource.driverClassName=com.mysql.jdbc.Driver
```

`redis` `rabbitmq` `mongodb` 等等中间件连接配置通过单独配置分开,以便后续方便修改ip等连接信息。

当然 `springboot` 在注入多数据源时要读取相应前缀的数据配置。

**代码:**

```
@Bean
@ConfigurationProperties(prefix = "spring.datasource.onemysql")
public DataSource originalDataSource(DataSourceFactory dataSourceFactory) {
    return DataSourceBuilder.create().build();
}
```

**配置:**

```
spring.datasource.onemysql.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.onemysql.url=jdbc:mysql://127.0.0.1:3306/${config.mysql.name}?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&autoReconnect=true
spring.datasource.onemysql.username=root
spring.datasource.onemysql.password=root
spring.datasource.onemysql.type=com.alibaba.druid.pool.DruidDataSource
spring.datasource.onemysql.driverClassName=com.mysql.jdbc.Driver
```


上面方式加载以 onemysql 开始的数据源配置。如果是多个的话，相应配置多个bean和配置文件。

根据上述方式, 我们可以使用一个mysql模板, 通过一定方式去加载创建对应的数据源。微服务下只需要维护一个mysql配置模板。

## 功能使用

### 添加依赖

**ps:** 实际version版本请使用最新版
**最新版本:** [![Maven Central](https://img.shields.io/maven-central/v/com.purgeteam/mysql-datasource-spring-boot-starter.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:com.purgeteam%20AND%20a:mysql-datasource-spring-boot-starter)

[点击查看最新新版本](https://search.maven.org/search?q=g:com.purgeteam%20AND%20a:mysql-datasource-spring-boot-starter)

```
<dependency>
  <groupId>com.purgeteam</groupId>
  <artifactId>mysql-datasource-spring-boot-starter</artifactId>
  <version>0.1.0.RELEASE</version>
</dependency>
```

### 1 配置模板

我们先按照原有的方式配置mysql数据源配置。

```
config.mysql.name=user
config.mysql.hosturl=127.0.0.1:3306

# mysql
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://${config.mysql.hosturl}/${config.mysql.name}?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&autoReconnect=true
spring.datasource.jdbc-url=${spring.datasource.url}
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
spring.datasource.driverClassName=com.mysql.jdbc.Driver
spring.datasource.filters=stat
spring.datasource.maxActive=20
spring.datasource.initialSize=1
spring.datasource.maxWait=60000
spring.datasource.minIdle=1
spring.datasource.timeBetweenEvictionRunsMillis=60000
spring.datasource.minEvictableIdleTimeMillis=300000
spring.datasource.validationQuery=select 'x'
spring.datasource.testWhileIdle=true
spring.datasource.testOnBorrow=false
spring.datasource.testOnReturn=false
spring.datasource.poolPreparedStatements=true
spring.datasource.maxOpenPreparedStatements=20
```

`config.mysql.name` 为数据库名称, 为下面代码注入做准备。

### 2 数据库名结合模板配置

通过 `DataSourceFactory#createDataSource` 方法可以将指定数据库注入。模板配置为之前的配置, 数据库名称通过 `${config.mysql.name}` 进行替换。

```
/**
 * 数据库名称替换方式生成 {@link DataSource}
 * @author purgeyao
 * @since 1.0
 */
@Configuration
public class OneDataSourceConfiguration {

    ...

    /**
     * DataSourceFactory#createDataSource 方式加载 one_mysql 数据库
     * @param dataSourceFactory dataSourceFactory
     * @return {@link DataSource}
     */
    @Primary
    @Bean
    public DataSource oneDataSource(DataSourceFactory dataSourceFactory) {
        return dataSourceFactory.createDataSource("one_mysql");
    }

}
```

这样可以把名为 `one_mysql` 数据库数据源进行加载。

### 2 数据库信息结合配置模板

当然只有上面方式只适合数据库地址密码一致的情况下,库名不一致,注入多数据源。

下面方式支持数据库信息不一致情况下使用模板注入多数据源。

需要先配置 mysql (1 配置模板),另行加下下面配置。

**配置:**

`config.datasource.mysql.source-info-map` 包含 4 个信息 `host-url数据库地址` 、 `name数据库名称`、`username数据库用户名`、 `password数据库密码`。

`config.datasource.mysql.source-info-map.tow_mysql` 里的 `tow_mysql` 为 map 集合的 key。

```
# mysql-datasource-spring-boot-starter
config.datasource.mysql.source-info-map.tow_mysql.host-url=127.0.0.1:3306
config.datasource.mysql.source-info-map.tow_mysql.name=tow_mysql
config.datasource.mysql.source-info-map.tow_mysql.username=root
config.datasource.mysql.source-info-map.tow_mysql.password=root
```

**代码:**

先在 `DataSourceConfigProperties` 对象里获取相应 `SourceInfoMap` 配置。

将 `DataSourceConfigProperties.SourceInfo towMysq` 配置通过 `DataSourceFactory#createDataSource` 方法进行创建即可。

```
/**
 * {@link DataSourceConfigProperties} 配置方式生成 {@link DataSource}
 *
 * @author purgeyao
 * @since 1.0
 */
@Configuration
public class TowDataSourceConfiguration {

    /**
     * DataSourceFactory#createDataSource 方式加载 tow_mysql 数据库
     *
     * @param dataSourceFactory dataSourceFactory
     * @return {@link DataSource}
     */
    @Bean
    public DataSource towDataSource(DataSourceFactory dataSourceFactory, DataSourceConfigProperties properties) {
        DataSourceConfigProperties.SourceInfo towMysql = properties.getSourceInfoMap().get("tow_mysql");
        if (towMysql == null) {
            throw new IllegalArgumentException("未获取到相应配置");
        }
        return dataSourceFactory.createDataSource(towMysql);
    }

}
```

### 4 注解选择器结合模板配置(推荐写法)

`@DataSourceSelector` 注解可以使用配置模板生成相应数据源对象。

`DataSourceSelector#value` 为数据库配置key(详情2的配置), 其他写法和原有的数据库创建方法一致。

只是把原有的 `@ConfigurationProperties(prefix = "spring.datasource.onemysql")` 替换为 `@DataSourceSelector("tow_mysql")`

```
/**
 * 注解方式注入 {@link DataSource}
 *
 * @author purgeyao
 * @since 1.0
 */
@Configuration
public class AnnotationsDataSourceConfiguration {

    ...

    /**
     * {@link DataSourceSelector} 方式选择注入 {@link DataSource}
     *
     * @return {@link DataSource}
     */
    @DataSourceSelector("tow_mysql")
    public DataSource selectorDataSource() {
        return DataSourceBuilder.create().build();
    }
}
```

## 总结
`@DataSourceSelector("tow_mysql")` 方式推荐使用。和之前的方法基本无差别, 简单方便点。

mysql 配置文件分开之后方便之后多个服务使用。


> qq交流群: `812321371`
> 微信交流群: `MercyYao`

> 示例代码地址:[data-source-spring-boot](https://github.com/purgeteam/data-source-spring-boot)

> 作者GitHub:
[Purgeyao](https://github.com/purgeyao) 欢迎关注
