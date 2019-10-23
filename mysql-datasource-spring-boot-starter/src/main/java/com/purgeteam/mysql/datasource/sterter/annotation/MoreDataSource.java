package com.purgeteam.mysql.datasource.sterter.annotation;

import java.lang.annotation.*;

/**
 * @author purgeyao
 * @since 1.0
 */
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MoreDataSource {

    /**
     * 数据库名称
     */
    String name() default "";

    /**
     * 数据库账号
     */
    String username() default "";

    /**
     * 数据库密码
     */
    String password() default "";

    /**
     * 数据库连接地址
     */
    String hostUrl() default "";
}
