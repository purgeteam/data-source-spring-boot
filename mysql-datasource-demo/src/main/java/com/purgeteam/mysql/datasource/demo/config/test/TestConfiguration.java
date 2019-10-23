package com.purgeteam.mysql.datasource.demo.config.test;

import com.purgeteam.mysql.datasource.sterter.MoreDataSourceProperties;
import com.purgeteam.mysql.datasource.sterter.DataSourceConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.annotation.Resource;

/**
 * @author purgeyao
 * @since 1.0
 */
@Order(0)
@Configuration
public class TestConfiguration {

    @Resource
    private MoreDataSourceProperties moreDataSourceProperties;

    @Resource
    private DataSourceConfigProperties dataSourceConfigProperties;

    @Bean
    public String test(){
        String name = moreDataSourceProperties.getName();
        String name1 = dataSourceConfigProperties.getName();
        return name;
    }
}
