package com.purgeteam.mysql.datasource.demo.config;

import com.purgeteam.mysql.datasource.sterter.DataSourceConfigProperties;
import com.purgeteam.mysql.datasource.sterter.factory.DataSourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.sql.DataSource;

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
