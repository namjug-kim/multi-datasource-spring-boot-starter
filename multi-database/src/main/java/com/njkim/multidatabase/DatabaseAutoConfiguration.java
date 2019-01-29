package com.njkim.multidatabase;

import com.njkim.multidatabase.model.CurrentTenantResolver;
import com.njkim.multidatabase.model.DatasourceBasedMultiTenantConnectionProvider;
import com.njkim.multidatabase.model.datasource.NamedDataSource;
import com.njkim.multidatabase.properties.MultiDatasourceProperties;
import com.njkim.multidatabase.properties.MultiJpaDatabaseProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Please describe the role of the DatabaseAutoConfiguration
 * <B>History:</B>
 * Created by namjug.kim on 2018. 9. 3.
 *
 * @author namjug.kim
 * @version 0.1
 * @since 2018. 9. 3.
 */
@Configuration
@ConditionalOnProperty(name = "multi.database.datasource.enable", havingValue = "true")
@EnableConfigurationProperties(MultiDatasourceProperties.class)
@EnableTransactionManagement(proxyTargetClass = true)
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class DatabaseAutoConfiguration {

    private final MultiDatasourceProperties properties;

    private final ApplicationContext applicationContext;

    public DatabaseAutoConfiguration(MultiDatasourceProperties properties, ApplicationContext applicationContext) {
        this.properties = properties;
        this.applicationContext = applicationContext;
    }

    @Bean
    public List<NamedDataSource> createDataSources() {
        List<MultiDatasourceProperties.NamedRoutingDataSourceTargetProperties> dataSourceTargetProperties = properties.getDataSources();

        return dataSourceTargetProperties.stream()
                .map(this::createNamedDatasource)
                .peek(this::registerBean)
                .collect(Collectors.toList());
    }

    private void registerBean(NamedDataSource namedDataSource) {
        ((ConfigurableListableBeanFactory) applicationContext.getAutowireCapableBeanFactory()).registerSingleton(namedDataSource.getName() + "-datasource", namedDataSource);
    }

    private NamedDataSource createNamedDatasource(MultiDatasourceProperties.NamedRoutingDataSourceTargetProperties properties) {
        HikariConfig hikariConfig = properties.getHikari();
        HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
        return NamedDataSource.create(properties.getName(), hikariDataSource);
    }

    @Configuration
    @ConditionalOnProperty(name = "multi.database.jpa.enable", havingValue = "true")
    @EnableConfigurationProperties(MultiJpaDatabaseProperties.class)
    @EnableTransactionManagement(proxyTargetClass = true)
    @EnableAspectJAutoProxy(proxyTargetClass = true)
    @AutoConfigureAfter(DatabaseAutoConfiguration.class)
    public class JpaAutoConfiguration {

        private final MultiJpaDatabaseProperties multiJpaDatabaseProperties;

        private final List<NamedDataSource> namedDataSources;

        @Autowired
        public JpaAutoConfiguration(MultiJpaDatabaseProperties multiJpaDatabaseProperties) {
            this.multiJpaDatabaseProperties = multiJpaDatabaseProperties;
            this.namedDataSources = createDataSources();
        }

        @Bean
        public CurrentTenantIdentifierResolver tenantIdentifierResolver() {
            return new CurrentTenantResolver();
        }

        @Bean
        public AbstractDataSourceBasedMultiTenantConnectionProviderImpl multiTenantConnectionProvider(List<NamedDataSource> namedDataSources) {
            Map<String, DataSource> dataSourceMap = namedDataSources.stream()
                    .collect(Collectors.toMap(NamedDataSource::getName, o -> (DataSource) o));

            return new DatasourceBasedMultiTenantConnectionProvider(dataSourceMap);
        }

        private HibernateJpaVendorAdapter vendorAdaptor() {
            HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
            vendorAdapter.setDatabasePlatform(multiJpaDatabaseProperties.getDatabasePlatform());
            vendorAdapter.setShowSql(true);
            vendorAdapter.setGenerateDdl(false);
            return vendorAdapter;
        }

        @Bean
        public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
            Map<String, Object> properties = new HashMap<>();
            properties.put(Environment.HBM2DDL_AUTO, multiJpaDatabaseProperties.getHibernate().getDdlAuto().getProperties());
            properties.put(Environment.DIALECT, multiJpaDatabaseProperties.getHibernate().getDialect());
            properties.put(Environment.SHOW_SQL, multiJpaDatabaseProperties.getHibernate().isShowSql());
            properties.put(Environment.FORMAT_SQL, multiJpaDatabaseProperties.getHibernate().isFormatSql());
            properties.put(Environment.PHYSICAL_NAMING_STRATEGY, multiJpaDatabaseProperties.getHibernate().getPhysicalNamingStrategy());

            properties.put(Environment.STATEMENT_BATCH_SIZE, multiJpaDatabaseProperties.getHibernate().getJdbcBatchSize());
            properties.put(Environment.MULTI_TENANT, "DATABASE");
            properties.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantIdentifierResolver());
            properties.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider(namedDataSources));

            multiJpaDatabaseProperties.getHibernate().getAdditional()
                    .forEach(properties::put);

            LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
            entityManagerFactoryBean.setPersistenceUnitName("defaultUnit");
            entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
            entityManagerFactoryBean.setPackagesToScan(multiJpaDatabaseProperties.getPackagesToScan());
            entityManagerFactoryBean.setJpaVendorAdapter(vendorAdaptor());
            entityManagerFactoryBean.setJpaPropertyMap(properties);

            return entityManagerFactoryBean;
        }

        @Bean
        public JpaTransactionManager transactionManager() {
            JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
            jpaTransactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
            return jpaTransactionManager;
        }
    }
}
