package com.njkim.multidatabase;

import com.njkim.multidatabase.model.CurrentTenantResolver;
import com.njkim.multidatabase.model.DatasourceBasedMultiTenantConnectionProvider;
import com.njkim.multidatabase.model.datasource.NamedDataSourceContainer;
import com.njkim.multidatabase.properties.MultiJpaDatabaseProperties;
import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

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
@ConditionalOnProperty(name = "multi.database.jpa.enable", havingValue = "true")
@EnableConfigurationProperties(MultiJpaDatabaseProperties.class)
@EnableTransactionManagement(proxyTargetClass = true)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@AutoConfigureAfter(NamedDataSourceAutoConfiguration.class)
public class JpaAutoConfiguration {
    private final MultiJpaDatabaseProperties multiJpaDatabaseProperties;

    @Autowired
    public JpaAutoConfiguration(MultiJpaDatabaseProperties multiJpaDatabaseProperties) {
        this.multiJpaDatabaseProperties = multiJpaDatabaseProperties;
    }

    @Bean
    public CurrentTenantIdentifierResolver tenantIdentifierResolver() {
        return new CurrentTenantResolver();
    }

    @Bean
    public AbstractDataSourceBasedMultiTenantConnectionProviderImpl multiTenantConnectionProvider(List<NamedDataSourceContainer> namedDataSourceContainers) {
        Map<String, NamedDataSourceContainer> dataSourceContainerMap = namedDataSourceContainers.stream()
                .collect(Collectors.toMap(NamedDataSourceContainer::getName, o -> o));

        return new DatasourceBasedMultiTenantConnectionProvider(dataSourceContainerMap);
    }

    private HibernateJpaVendorAdapter vendorAdaptor() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabasePlatform(multiJpaDatabaseProperties.getDatabasePlatform());
        vendorAdapter.setShowSql(true);
        vendorAdapter.setGenerateDdl(false);
        return vendorAdapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(MultiTenantConnectionProvider multiTenantConnectionProvider) {
        Map<String, Object> properties = new HashMap<>();

        if (multiJpaDatabaseProperties.getHibernate().getDdlAuto() != null) {
            properties.put(Environment.HBM2DDL_AUTO, multiJpaDatabaseProperties.getHibernate().getDdlAuto().getProperties());
        }
        properties.put(Environment.DIALECT, multiJpaDatabaseProperties.getHibernate().getDialect());
        properties.put(Environment.SHOW_SQL, multiJpaDatabaseProperties.getHibernate().isShowSql());
        properties.put(Environment.FORMAT_SQL, multiJpaDatabaseProperties.getHibernate().isFormatSql());
        properties.put(Environment.PHYSICAL_NAMING_STRATEGY, multiJpaDatabaseProperties.getHibernate().getPhysicalNamingStrategy());

        properties.put(Environment.STATEMENT_BATCH_SIZE, multiJpaDatabaseProperties.getHibernate().getJdbcBatchSize());
        properties.put(Environment.MULTI_TENANT, "DATABASE");
        properties.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantIdentifierResolver());
        properties.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);

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
    public JpaTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return jpaTransactionManager;
    }
}
