package com.njkim.multidatabase.properties;

import com.zaxxer.hikari.HikariConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

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
@ConfigurationProperties(prefix = "multi.database.datasource")
public class MultiDatasourceProperties {

    private boolean enable;

    private List<NamedRoutingDataSourceTargetProperties> dataSources;

    @Getter
    @Setter
    public static class NamedRoutingDataSourceTargetProperties {
        private String name;
        private HikariConfig master;
        private List<HikariConfig> slaves = new ArrayList<>();
    }
}
