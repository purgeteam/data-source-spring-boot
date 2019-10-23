package com.purgeteam.mysql.datasource.sterter.factory;

/**
 * @author purgeyao
 * @since 1.0
 */
public enum DataSourceType {

    /**
     * mysql 头部
     * jdbc:mysql://127.0.0.1:3306/one_mysql?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&autoReconnect=true
     */
    MYSQL_HEAD("jdbc:mysql://");

    private String value;

    DataSourceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
