package com.purgeteam.mysql.datasource.sterter;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author purgeyao
 * @since 1.0
 */
@ConfigurationProperties(DataSourceConfigProperties.PREFIX)
public class DataSourceConfigProperties {

    public static final String PREFIX = "config.datasource.mysql";

    private String name;

    private String hostUrl;

    private Map<String, SourceInfo> sourceInfoMap = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHostUrl() {
        return hostUrl;
    }

    public void setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
    }

    public Map<String, SourceInfo> getSourceInfoMap() {
        return sourceInfoMap;
    }

    public void setSourceInfoMap(Map<String, SourceInfo> sourceInfoMap) {
        this.sourceInfoMap = sourceInfoMap;
    }

    public static class SourceInfo {

        /**
         * 数据库名称
         */
        private String name;

        /**
         * 数据库账号
         */
        private String username;

        /**
         * 数据库密码
         */
        private String password;

        /**
         * 数据库连接地址
         */
        private String hostUrl;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getHostUrl() {
            return hostUrl;
        }

        public void setHostUrl(String hostUrl) {
            this.hostUrl = hostUrl;
        }
    }
}
