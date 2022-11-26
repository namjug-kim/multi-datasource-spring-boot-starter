package com.njkim.multidatabase;

import com.njkim.multidatabase.properties.MultiDatasourceProperties;
import com.zaxxer.hikari.HikariConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class JpaAutoConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(JpaAutoConfiguration.class));

    @Test
    public void jpaBeanCreate() {

        this.contextRunner
                .withPropertyValues(
                        "multi.database.datasource.enable=true",
                        "multi.database.jpa.enable=true",
                        "multi.database.jpa.database-platform=H2",
                        "multi.database.jpa.packages-to-scan=com.njkim.multidatabase",
                        "multi.database.jpa.hibernate.ddl-auto=NONE",
                        "multi.database.jpa.hibernate.jdbc-batch-size=1",
                        "multi.database.jpa.hibernate.dialect= org.hibernate.dialect.H2Dialect"
                )
                .withUserConfiguration(DatabaseSourceAutoConfigurationTest.MultiDataSourceConfig.class, MultiDatasourceMetricConfiguration.class)
                .run((context) ->
                        assertThat(context)
                                .hasBean("entityManagerFactory")
                                .hasBean("transactionManager")
                                .hasBean("multiTenantConnectionProvider")
                                .hasBean("tenantIdentifierResolver")
                );
    }

    @Configuration
    static class MultiDataSourceConfig {

        @Bean
        @Primary
        public MultiDatasourceProperties multiDatasourceProperties() {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl("jdbc:h2:mem:master;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;IGNORECASE=TRUE");
            hikariConfig.setUsername("sa");
            hikariConfig.setPassword("sa");

            MultiDatasourceProperties.NamedRoutingDataSourceTargetProperties defaultDatasourceProperties = new MultiDatasourceProperties.NamedRoutingDataSourceTargetProperties();
            defaultDatasourceProperties.setName("default");
            defaultDatasourceProperties.setMaster(hikariConfig);

            MultiDatasourceProperties.NamedRoutingDataSourceTargetProperties secondDatasourceProperties = new MultiDatasourceProperties.NamedRoutingDataSourceTargetProperties();
            secondDatasourceProperties.setName("second");
            secondDatasourceProperties.setMaster(hikariConfig);

            MultiDatasourceProperties multiDatasourceProperties = new MultiDatasourceProperties();
            multiDatasourceProperties.setEnable(true);
            multiDatasourceProperties.setDataSources(Arrays.asList(defaultDatasourceProperties, secondDatasourceProperties));

            return multiDatasourceProperties;
        }
    }
}
