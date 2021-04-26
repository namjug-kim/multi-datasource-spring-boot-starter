package com.njkim.multidatabase.model;

import com.njkim.multidatabase.model.datasource.NamedDataSourceContainer;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.util.Map;

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

    private final Map<String, NamedDataSourceContainer> dataSourceMap;

    public DatasourceBasedMultiTenantConnectionProvider(Map<String, NamedDataSourceContainer> dataSourceMap) {
        this.dataSourceMap = dataSourceMap;
    }

    @Override
    protected DataSource selectAnyDataSource() {
        return selectDataSource("default");
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        NamedDataSourceContainer namedDataSourceContainer = dataSourceMap.get(tenantIdentifier);

        if (namedDataSourceContainer == null) {
            throw new RuntimeException("Not found Data Source for " + tenantIdentifier);
        }

        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        return namedDataSourceContainer.getDataSource(isReadOnly);
    }
}
