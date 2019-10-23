package com.purgeteam.mysql.datasource.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.sql.DataSource;

@Slf4j
@SpringBootApplication
public class DataSourceSpringBootApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(DataSourceSpringBootApplication.class, args);
        DataSource bean = run.getBean(DataSource.class);
        DataSource selectorDataSource = run.getBean("selectorDataSource", DataSource.class);
        log.info("测试");
    }

}
