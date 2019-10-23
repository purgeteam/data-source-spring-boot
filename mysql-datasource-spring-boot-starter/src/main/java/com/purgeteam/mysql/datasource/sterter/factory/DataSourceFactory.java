package com.purgeteam.mysql.datasource.sterter.factory;

import com.alibaba.druid.pool.DruidDataSource;
import com.purgeteam.mysql.datasource.sterter.DataSourceConfigProperties;
import com.purgeteam.mysql.datasource.sterter.MoreDataSourceProperties;
import com.purgeteam.mysql.datasource.sterter.annotation.DataSourceSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertyNameAliases;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Map;

/**
 * {@link DataSource} 构建工厂
 *
 * @author purgeyao
 * @since 1.0
 */
@Configuration
public class DataSourceFactory implements EnvironmentAware {

    private static Logger log = LoggerFactory.getLogger(DataSourceFactory.class);

    private static final String MYSQL_NAME = "${config.mysql.name}";
    private static final String NAME_KEY = "config.mysql.name";
    private static final String MYSQL_HOST_URL = "${config.mysql.hosturl}";
    private static final String HOST_URL_KEY = "config.mysql.hosturl";
    private static final String DEFAULT_DATASOURCE_URL = "spring.datasource.url";
    public static final String DEFAULT_DATASOURCE_PREFIX = "spring.datasource";

    private Environment evn;

    /**
     * 参数绑定工具
     */
    private Binder binder;

    /**
     * 格式化
     */
    private static final ConfigurationPropertyNameAliases ALIASES = new ConfigurationPropertyNameAliases();

    /**
     * create {@link DruidDataSource} data source, dataSourceName参数默认会执行替换 spring.datasource.url
     * 内的数据库名操作。 兼容多数据源创建操作，支持 ${config.mysql.name} nacos 数据源模板。
     *
     * @param dataSourceName 数据库名称
     */
    public DataSource createDataSource(String dataSourceName) {

        // url 组装
        String url = urlProcessor(dataSourceName);

        // 创建数据源绑定参数
        Map dataSourceProperties = binder.bind(DEFAULT_DATASOURCE_PREFIX, Map.class).get();
        DruidDataSource datasource = new DruidDataSource();
        bind(datasource, dataSourceProperties);
        datasource.setUrl(url);
        log.info("[createDataSource] Generate {} DruidDataSource", dataSourceName);
        return datasource;
    }

    /**
     * create {@link DruidDataSource} data source, SourceInfo 对象方式创建
     *
     * @param config {@link DataSourceConfigProperties.SourceInfo} 对象 包含 数据库用户名 密码 地址
     * @return {@link DataSource}
     */
    public DataSource createDataSource(DataSourceConfigProperties.SourceInfo config) {
        // 创建数据源绑定参数
        Map dataSourceProperties = binder.bind(DEFAULT_DATASOURCE_PREFIX, Map.class).get();
        DruidDataSource druidDataSource = new DruidDataSource();
        bind(druidDataSource, dataSourceProperties);

        // url 组装
        String url = urlMaking(config.getHostUrl(), config.getName());
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(config.getUsername());
        druidDataSource.setPassword(config.getPassword());
        log.info("[createDataSource] Generate {} DruidDataSource", druidDataSource.getName());
        return druidDataSource;
    }

    /**
     * 生成数据库连接
     * 支持原生url 支持定制化 如 存在占位符 ${config.mysql.name}等 主要获取原始 spring.datasource.url 里 `?` 后面参数
     *
     * @param hostUrl        数据库地址
     * @param dataSourceName 数据库名称
     * @return url
     */
    public String urlMaking(String hostUrl, String dataSourceName) {
        String url = evn.getProperty(DEFAULT_DATASOURCE_URL);
        if (StringUtils.isEmpty(url)) {
            throw new IllegalArgumentException(
                    String.format("[createDataSource] %s data source url is null", DEFAULT_DATASOURCE_URL));
        }
        if (StringUtils.isEmpty(hostUrl)) {
            throw new IllegalArgumentException("[createDataSource] hostUrl is null");
        }
        if (StringUtils.isEmpty(dataSourceName)) {
            throw new IllegalArgumentException("[createDataSource] dataSourceName is null");
        }
        StringBuilder sb = new StringBuilder(DataSourceType.MYSQL_HEAD.getValue());
        sb.append(hostUrl).append("/").append(dataSourceName);
        int i = url.indexOf("?");
        if (i != -1) {
            sb.append(url.substring(i));
        }
        return sb.toString();
    }

    /**
     * 数据库连接加工
     *
     * @param dataSourceName 数据库名称
     * @return url
     */
    private String urlProcessor(String dataSourceName) {

        // jdbc:mysql://10.1.1.97:5511/one_mysql?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&autoReconnect=true

        String url = evn.getProperty(DEFAULT_DATASOURCE_URL);
        if (StringUtils.isEmpty(url)) {
            throw new IllegalArgumentException(
                    String.format("[createDataSource] %s data source url is null", DEFAULT_DATASOURCE_URL));
        }

        // 数据库名称替换策略
        if (dataSourceName != null) {
            String defaultName = evn.getProperty(NAME_KEY);
            if (!StringUtils.isEmpty(defaultName)) {
                url = url.replace(defaultName, dataSourceName);
            } else {
                url = url.replace(MYSQL_NAME, dataSourceName);
            }
        }
        return url;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.evn = environment;
        // 绑定配置器
        binder = Binder.get(evn);
    }

    /**
     * 绑定参数，DataSourceBuilder的bind方法实现
     *
     * @param result     {@link DataSource}
     * @param properties {@link Map}
     */
    @SuppressWarnings("all")
    private void bind(DataSource result, Map properties) {
        ConfigurationPropertySource source = new MapConfigurationPropertySource(properties);
        Binder binder = new Binder(new ConfigurationPropertySource[]{source.withAliases(ALIASES)});
        // 将参数绑定到对象
        binder.bind(ConfigurationPropertyName.EMPTY, Bindable.ofInstance(result));
    }

    @SuppressWarnings("all")
    private <T extends DataSource> T bind(Class<T> clazz, Map properties) {
        ConfigurationPropertySource source = new MapConfigurationPropertySource(properties);
        Binder binder = new Binder(new ConfigurationPropertySource[]{source.withAliases(ALIASES)});
        // 通过类型绑定参数并获得实例对象
        return binder.bind(ConfigurationPropertyName.EMPTY, Bindable.of(clazz)).get();
    }

    /**
     * @param clazz      {@link Class<T>}
     * @param sourcePath {@link String} sourcePath 参数路径，对应配置文件中的值，如: spring.datasource
     * @param <T>        {@link <T extends DataSource>}
     * @return {@link <T extends DataSource>}
     */
    private <T extends DataSource> T bind(Class<T> clazz, String sourcePath) {
        Map properties = binder.bind(sourcePath, Map.class).get();
        return bind(clazz, properties);
    }

}
