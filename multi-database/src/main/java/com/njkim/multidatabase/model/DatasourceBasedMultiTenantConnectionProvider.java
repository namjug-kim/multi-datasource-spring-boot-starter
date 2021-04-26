package com.njkim.multidatabase.model;

import com.njkim.multidatabase.model.datasource.NamedDataSourceContainer;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Please describe the role of the DatasourceBasedMultiTenantConnectionProvider
 * <B>History:</B>
 * Created by namjug.kim on 2018. 9. 3.
 *
 * @author namjug.kim
 * @version 0.1
 * @since 2018. 9. 3.
 */
public class DatasourceBasedMultiTenantConnectionProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    private final Map<String, DataSource> dataSourceMap;

    public DatasourceBasedMultiTenantConnectionProvider(Map<String, NamedDataSourceContainer> dataSourceMap) {
        this.dataSourceMap = new HashMap<>();
        dataSourceMap.forEach((tenantId, namedDataSourceContainer) ->
                this.dataSourceMap.put(tenantId, new LazyConnectionDataSourceProxy(new ReplicationRoutingDataSource(namedDataSourceContainer)))
        );
    }

    @Override
    protected DataSource selectAnyDataSource() {
        return selectDataSource("default");
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        DataSource dataSource = dataSourceMap.get(tenantIdentifier);

        if (dataSource == null) {
            throw new RuntimeException("Not found Data Source for " + tenantIdentifier);
        }

        return dataSource;
    }
}

class ReplicationRoutingDataSource extends AbstractRoutingDataSource {
    private static final String MASTER_NAME = "master";
    private static final String SLAVE_NAME = "slave";
    private final AtomicInteger index = new AtomicInteger(0);
    private final int slaveSize;

    public ReplicationRoutingDataSource(NamedDataSourceContainer namedDataSourceContainer) {
        this.slaveSize = namedDataSourceContainer.getSlaves().size();

        Map<Object, Object> namedDataSourceMap = new HashMap<>();
        namedDataSourceMap.put(MASTER_NAME, namedDataSourceContainer.getMaster());
        int index = 0;
        for (DataSource slave : namedDataSourceContainer.getSlaves()) {
            namedDataSourceMap.put(SLAVE_NAME + index++, slave);
        }
        this.setTargetDataSources(namedDataSourceMap);
        this.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        if (!isReadOnly) {
            return MASTER_NAME;
        } else {
            return SLAVE_NAME + (index.incrementAndGet() % slaveSize);
        }
    }
}
