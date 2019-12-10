package com.njkim.multidatabase;

import com.njkim.multidatabase.aspect.DatabaseSelector;
import com.njkim.multidatabase.model.datasource.NamedDataSource;
import com.njkim.multidatabase.properties.MultiDatasourceProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@ConditionalOnProperty(name = "multi.database.datasource.enable", havingValue = "true")
@EnableConfigurationProperties(MultiDatasourceProperties.class)
@EnableTransactionManagement(proxyTargetClass = true)
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class NamedDataSourceAutoConfiguration {
    private final MultiDatasourceProperties properties;

    private final GenericApplicationContext applicationContext;

    public NamedDataSourceAutoConfiguration(MultiDatasourceProperties properties, GenericApplicationContext applicationContext) {
        this.properties = properties;
        this.applicationContext = applicationContext;
        createDataSourcesBean();
    }

    @Bean
    public DatabaseSelector databaseSelector() {
        return new DatabaseSelector();
    }

    public void createDataSourcesBean() {
        List<MultiDatasourceProperties.NamedRoutingDataSourceTargetProperties> dataSourceTargetProperties = properties.getDataSources();

        dataSourceTargetProperties.forEach(dataSourceProperties -> {
            applicationContext.registerBean(dataSourceProperties.getName() + "-datasource", DataSource.class, () -> createNamedDatasource(dataSourceProperties));
        });
    }

    private NamedDataSource createNamedDatasource(MultiDatasourceProperties.NamedRoutingDataSourceTargetProperties properties) {
        HikariConfig hikariConfig = properties.getHikari();
        HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
        return NamedDataSource.create(properties.getName(), hikariDataSource);
    }
}
