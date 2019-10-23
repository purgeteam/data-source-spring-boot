package com.purgeteam.mysql.datasource.sterter.annotation;

import org.springframework.context.annotation.Bean;

import java.lang.annotation.*;

/**
 * 数据源配置选择器
 *
 * @author purgeyao
 * @since 1.0
 */
@Bean
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSourceSelector {

    /**
     * 数据库标签
     */
    String value() default "";
}
