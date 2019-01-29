package com.njkim.multidatabase.model.datasource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Delegate;

import javax.sql.DataSource;

/**
 * Please describe the role of the NamedDataSource
 * <B>History:</B>
 * Created by namjug.kim on 2018. 9. 3.
 *
 * @author namjug.kim
 * @version 0.1
 * @since 2018. 9. 3.
 */
@Getter
@AllArgsConstructor
public class NamedDataSource implements DataSource {

    private final String name;

    @Delegate(types = DataSource.class)
    private final DataSource delegate;

    public static NamedDataSource create(String name, DataSource delegate) {
        return new NamedDataSource(name, delegate);
    }
}
