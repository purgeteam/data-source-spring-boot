package com.purgeteam.mysql.datasource.sterter;

import com.purgeteam.mysql.datasource.sterter.context.properties.DataSourcePropertiesBindingPostProcessor;
import com.purgeteam.mysql.datasource.sterter.factory.DataSourceFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author purgeyao
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties(DataSourceConfigProperties.class)
public class MysqlDataSourceConfiguration {

    @Bean
    public DataSourceFactory dataSourceFactory() {
        return new DataSourceFactory();
    }

    @Bean
    public DataSourcePropertiesBindingPostProcessor dataSourcePropertiesBindingPostProcessor() {
        return new DataSourcePropertiesBindingPostProcessor();
    }
}
