package com.purgeteam.mysql.datasource.sterter.context.properties;

import com.purgeteam.mysql.datasource.sterter.DataSourceConfigProperties;
import com.purgeteam.mysql.datasource.sterter.annotation.DataSourceSelector;
import com.purgeteam.mysql.datasource.sterter.factory.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.ConfigurationBeanFactoryMetadata;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertyNameAliases;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author purgeyao
 * @since 1.0
 */
public class DataSourcePropertiesBindingPostProcessor implements BeanPostProcessor, PriorityOrdered, ApplicationContextAware, InitializingBean {

    private static Logger log = LoggerFactory.getLogger(DataSourcePropertiesBindingPostProcessor.class);

    private static final String URL_KEY = "url";
    private static final String JDBC_URL_KEY = "jdbc-url";
    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";

    private ApplicationContext applicationContext;

    private ConfigurationBeanFactoryMetadata beanFactoryMetadata;

    /**
     * 参数绑定工具
     */
    private Binder binder;

    private DataSourceFactory dataSourceFactory;

    /**
     * 格式化
     */
    private static final ConfigurationPropertyNameAliases ALIASES = new ConfigurationPropertyNameAliases();

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        DataSourceSelector annotation = getAnnotation(bean, beanName, DataSourceSelector.class);
        if (annotation != null) {
            bind(bean, annotation);
            log.info("[DataSourceSelector] The {} database object was successfully initialized", annotation.value());
        }
        return bean;
    }

    private void bind(Object bean, DataSourceSelector annotation) {
        Map dataSourceProperties = binder.bind(DataSourceFactory.DEFAULT_DATASOURCE_PREFIX, Map.class).get();

        String value = annotation.value();
        Map test = binder.bind(DataSourceConfigProperties.PREFIX, Map.class).get();
        LinkedHashMap sourceInfoMap = (LinkedHashMap) test.get("source-info-map");
        Object sourceInfo = sourceInfoMap.get(value);
        if (StringUtils.isEmpty(sourceInfo)) {
            throw new IllegalArgumentException(String.format("MoreDataSourceProperties.SourceInfo %s data is null", value));
        }
        LinkedHashMap source = (LinkedHashMap) sourceInfo;
        Object hostUrl = source.get("host-url");
        if (hostUrl == null) {
            hostUrl = source.get("hostUrl");
        }
        String url = dataSourceFactory.urlMaking(hostUrl.toString(), source.get("name").toString());
        dataSourceProperties.put(JDBC_URL_KEY, url);
        dataSourceProperties.put(URL_KEY, url);
        dataSourceProperties.put(USERNAME_KEY, source.get(USERNAME_KEY));
        dataSourceProperties.put(PASSWORD_KEY, source.get(PASSWORD_KEY));
        bind(bean, dataSourceProperties);
    }

    private <A extends Annotation> A getAnnotation(Object bean, String beanName, Class<A> type) {
        A annotation = this.beanFactoryMetadata.findFactoryAnnotation(beanName, type);
        if (annotation == null) {
            annotation = AnnotationUtils.findAnnotation(bean.getClass(), type);
        }
        return annotation;
    }

    /**
     * 绑定参数，DataSourceBuilder的bind方法实现
     *
     * @param result     {@link DataSource}
     * @param properties {@link Map}
     */
    @SuppressWarnings("all")
    private void bind(Object result, Map properties) {
        ConfigurationPropertySource source = new MapConfigurationPropertySource(properties);
        Binder binder = new Binder(new ConfigurationPropertySource[]{source.withAliases(ALIASES)});
        // 将参数绑定到对象
        binder.bind(ConfigurationPropertyName.EMPTY, Bindable.ofInstance(result));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        binder = Binder.get(applicationContext.getEnvironment());
        this.beanFactoryMetadata = this.applicationContext.getBean(ConfigurationBeanFactoryMetadata.BEAN_NAME,
                ConfigurationBeanFactoryMetadata.class);
        this.dataSourceFactory = this.applicationContext.getBean(DataSourceFactory.class);
    }
}
