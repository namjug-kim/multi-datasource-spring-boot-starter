package com.njkim.multidatabase.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Please describe the role of the MultiDatasourceProperties
 * <B>History:</B>
 * Created by namjug.kim on 2018. 9. 3.
 *
 * @author namjug.kim
 * @version 0.1
 * @since 2018. 9. 3.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "multi.database.jpa")
public class MultiJpaDatabaseProperties {

    private boolean enable;

    private String[] packagesToScan;

    private String databasePlatform;

    private HibernateConfig hibernate;

    @Getter
    @Setter
    public static class HibernateConfig {

        private DdlAutoType ddlAuto;
        private String dialect;
        private boolean showSql;
        private boolean formatSql;
        private String physicalNamingStrategy;
        private int jdbcBatchSize;

        private Map<String, String> additional = Collections.emptyMap();
    }
}
