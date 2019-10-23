package com.purgeteam.mysql.datasource.demo.config;

import com.purgeteam.mysql.datasource.sterter.factory.DataSourceFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 数据库名称替换方式生成 {@link DataSource}
 * @author purgeyao
 * @since 1.0
 */
@Configuration
public class OneDataSourceConfiguration {

    /**
     * 原始写法 通过 {@link ConfigurationProperties} 加载前缀多数据源
     * @param dataSourceFactory dataSourceFactory
     * @return {@link DataSource}
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource originalDataSource(DataSourceFactory dataSourceFactory) {
        return DataSourceBuilder.create().build();
    }

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
