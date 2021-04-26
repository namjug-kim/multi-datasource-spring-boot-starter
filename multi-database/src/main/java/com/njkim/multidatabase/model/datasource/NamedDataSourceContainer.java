package com.njkim.multidatabase.model.datasource;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Please describe the role of the NamedDataSourceContainer
 * <B>History:</B>
 * Created by namjug.kim on 2018. 9. 3.
 *
 * @author namjug.kim
 * @version 0.1
 * @since 2018. 9. 3.
 */
@Getter
@AllArgsConstructor
public class NamedDataSourceContainer {
    private final String name;

    private final DataSource master;

    private final List<DataSource> slaves;

    private final AtomicInteger routingIndex = new AtomicInteger(0);

    public static NamedDataSourceContainer create(String name, DataSource master, List<DataSource> slaves) {
        if (slaves.isEmpty()) {
            return new NamedDataSourceContainer(name, master, Collections.singletonList(master));
        } else {
            return new NamedDataSourceContainer(name, master, slaves);
        }
    }

    public DataSource getDataSource(boolean isReadOnly) {
        if (!isReadOnly) {
            return master;
        } else {
            int routingIndex = this.routingIndex.incrementAndGet();
            return slaves.get(routingIndex % slaves.size());
        }
    }
}
