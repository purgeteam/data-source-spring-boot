package com.purgeteam.mysql.datasource.demo.config;

import com.purgeteam.mysql.datasource.sterter.annotation.DataSourceSelector;
import com.purgeteam.mysql.datasource.sterter.annotation.MoreDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 注解方式注入 {@link DataSource}
 *
 * @author purgeyao
 * @since 1.0
 */
@Configuration
public class AnnotationsDataSourceConfiguration {

    /**
     *  todo 待完善
     * 原始写法 通过 {@link MoreDataSource} 加载数据源 (功能待实现)
     *
     * @return {@link DataSource}
     */
    @Bean
    @MoreDataSource(name = "annotations_mysql", username = "123123", password = "123123", hostUrl = "127.0.0.1:3306")
    public DataSource annotationsDataSource() {
        return DataSourceBuilder.create().build();
    }

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
