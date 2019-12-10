package com.njkim.multidatabase;

import com.njkim.multidatabase.properties.MultiDatasourceProperties;
import com.zaxxer.hikari.HikariConfig;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DatabaseSourceAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(NamedDataSourceAutoConfiguration.class, JpaAutoConfiguration.class));

    @Test
    public void singleDataSourceBeanCreate() {
        this.contextRunner
                .withPropertyValues("multi.database.datasource.enable=true")
                .withUserConfiguration(MultiDataSourceConfig.class)
                .run((context) ->
                        assertThat(context)
                                .hasBean("default-datasource")
                );
    }

    @Test
    public void multiDataSourceBeanCreate() {
        this.contextRunner
                .withPropertyValues("multi.database.datasource.enable=true")
                .withUserConfiguration(MultiDataSourceConfig.class)
                .run((context) ->
                        assertThat(context)
                                .hasBean("default-datasource")
                                .hasBean("second-datasource")
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
            namedRoutingDataSourceTargetProperties.setHikari(hikariConfig);

            MultiDatasourceProperties multiDatasourceProperties = new MultiDatasourceProperties();
            multiDatasourceProperties.setEnable(true);
            multiDatasourceProperties.setDataSources(Arrays.asList(namedRoutingDataSourceTargetProperties));

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
            defaultDatasourceProperties.setHikari(hikariConfig);

            MultiDatasourceProperties.NamedRoutingDataSourceTargetProperties secondDatasourceProperties = new MultiDatasourceProperties.NamedRoutingDataSourceTargetProperties();
            secondDatasourceProperties.setName("second");
            secondDatasourceProperties.setHikari(hikariConfig);

            MultiDatasourceProperties multiDatasourceProperties = new MultiDatasourceProperties();
            multiDatasourceProperties.setEnable(true);
            multiDatasourceProperties.setDataSources(Arrays.asList(defaultDatasourceProperties, secondDatasourceProperties));

            return multiDatasourceProperties;
        }
    }
}
