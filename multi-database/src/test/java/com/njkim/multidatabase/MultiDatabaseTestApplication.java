package com.njkim.multidatabase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * Please describe the role of the MultiDatabaseTestApplication
 * <B>History:</B>
 * Created by namjug.kim on 2019-01-29
 *
 * @author namjug.kim
 * @version 0.1
 * @since 2019-01-29
 */
@SpringBootApplication
@Import(DatabaseAutoConfiguration.class)
public class MultiDatabaseTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(MultiDatabaseTestApplication.class, args);
    }
}
