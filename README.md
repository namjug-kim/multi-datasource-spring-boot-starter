# Multi Datasource Spring boot starter
# Table of Contents
 
- [Overview](#overview)
- [Getting started](#getting-started)

### Overview

### Getting started

#### Configuration

The following default properties can be configured via properties file:

```yaml
multi:
  database:
    jpa:
      enable: true
      database-platform: H2
      packages-to-scan: com.njkim.example.database
      hibernate:
        jdbc-batch-size: 1
        dialect: org.hibernate.dialect.H2Dialect
        format-sql: true
        show-sql: true
        ddl-auto: VALIDATE
    datasource:
      enable: true
      data-sources:
      - name: default
        master:
          jdbc-url: jdbc:h2:mem:master;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;IGNORECASE=TRUE
          username: sa
          password: sa
          maximumPoolSize: 13
          minimumIdle: 3
          maxLifetime: 900000
          connectionTimeout: 10000
          connectionTestQuery: SELECT 1
      - name: second
        master:
          jdbc-url: jdbc:h2:mem:second;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;IGNORECASE=TRUE
          username: sa
          password: sa
          maximumPoolSize: 13
          minimumIdle: 3
          maxLifetime: 900000
          connectionTimeout: 10000
          connectionTestQuery: SELECT 1

    
```

### License

Multi Datasource Spring boot starter is licensed under the MIT License. See [LICENSE](LICENSE.md) for details.
