package com.njkim.multidatabase;

import com.njkim.multidatabase.properties.MultiDatasourceProperties;
import com.zaxxer.hikari.HikariConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DatabaseSourceAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(JpaAutoConfiguration.class));

    @Test
    public void singleDataSourceBeanCreate() {
        this.contextRunner
                .withPropertyValues(
                        "multi.database.jpa.enable=true",
                        "multi.database.jpa.database-platform=H2",
                        "multi.database.jpa.packages-to-scan=com.njkim.multidatabase",
                        "multi.database.jpa.hibernate.ddl-auto=NONE",
                        "multi.database.jpa.hibernate.jdbc-batch-size=1",
                        "multi.database.jpa.hibernate.dialect= org.hibernate.dialect.H2Dialect"
                )
                .withUserConfiguration(MultiDataSourceConfig.class)
                .run((context) ->
                        assertThat(context)
                                .hasBean("default-master-datasource")
                );
    }

    @Test
    public void multiDataSourceBeanCreate() {
        this.contextRunner
                .withPropertyValues(
                        "multi.database.jpa.enable=true",
                        "multi.database.jpa.database-platform=H2",
                        "multi.database.jpa.packages-to-scan=com.njkim.multidatabase",
                        "multi.database.jpa.hibernate.ddl-auto=NONE",
                        "multi.database.jpa.hibernate.jdbc-batch-size=1",
                        "multi.database.jpa.hibernate.dialect= org.hibernate.dialect.H2Dialect"
                )
                .withUserConfiguration(MultiDataSourceConfig.class)
                .run((context) ->
                        assertThat(context)
                                .hasBean("default-master-datasource")
                                .hasBean("second-master-datasource")
                );
    }

    @Configuration
    static class SingleDataSourceConfig {

        @Bean
        @Primary
        public MultiDatasourceProperties multiDatasourceProperties() {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl("jdbc:h2:mem:master;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;IGNORECASE=TRUE");
            hikariConfig.setUsername("sa");
            hikariConfig.setPassword("sa");

            MultiDatasourceProperties.NamedRoutingDataSourceTargetProperties namedRoutingDataSourceTargetProperties = new MultiDatasourceProperties.NamedRoutingDataSourceTargetProperties();
            namedRoutingDataSourceTargetProperties.setName("default");
            namedRoutingDataSourceTargetProperties.setMaster(hikariConfig);

            MultiDatasourceProperties multiDatasourceProperties = new MultiDatasourceProperties();
            multiDatasourceProperties.setEnable(true);
            multiDatasourceProperties.setDataSources(Collections.singletonList(namedRoutingDataSourceTargetProperties));

            return multiDatasourceProperties;
        }
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
