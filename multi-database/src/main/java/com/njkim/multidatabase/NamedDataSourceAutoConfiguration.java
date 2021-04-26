package com.njkim.multidatabase;

import com.njkim.multidatabase.aspect.DatabaseSelector;
import com.njkim.multidatabase.model.datasource.NamedDataSourceContainer;
import com.njkim.multidatabase.properties.MultiDatasourceProperties;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@ConditionalOnProperty(name = "multi.database.datasource.enable", havingValue = "true")
@EnableConfigurationProperties(MultiDatasourceProperties.class)
@EnableTransactionManagement(proxyTargetClass = true)
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class NamedDataSourceAutoConfiguration {
    private final MultiDatasourceProperties properties;

    private final AnnotationConfigApplicationContext applicationContext;

    public NamedDataSourceAutoConfiguration(MultiDatasourceProperties properties, AnnotationConfigApplicationContext applicationContext) {
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
            NamedDataSourceContainer namedDatasourceContainer = createNamedDatasourceContainer(dataSourceProperties);
            applicationContext.registerBean(dataSourceProperties.getName() + "-master-datasource", DataSource.class, namedDatasourceContainer::getMaster);
            namedDatasourceContainer.getSlaves().forEach(slaveDataSource -> applicationContext.registerBean(dataSourceProperties.getName() + "-slave-datasource", DataSource.class, () -> slaveDataSource));
        });
    }

    private NamedDataSourceContainer createNamedDatasourceContainer(MultiDatasourceProperties.NamedRoutingDataSourceTargetProperties properties) {
        DataSource masterDataSource = new HikariDataSource(properties.getMaster());
        List<DataSource> slaveDataSources = properties.getSlaves().stream()
                .map(HikariDataSource::new)
                .collect(Collectors.toList());
        return NamedDataSourceContainer.create(properties.getName(), masterDataSource, slaveDataSources);
    }
}
