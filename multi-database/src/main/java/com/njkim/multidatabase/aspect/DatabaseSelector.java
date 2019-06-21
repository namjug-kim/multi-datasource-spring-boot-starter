package com.njkim.multidatabase.aspect;

import com.njkim.multidatabase.annotation.SelectDB;
import com.njkim.multidatabase.model.TenantContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * Please describe the role of the DatabaseSelector
 * <B>History:</B>
 * Created by namjug.kim on 2018. 9. 3.
 *
 * @author namjug.kim
 * @version 0.1
 * @since 2018. 9. 3.
 */
@Slf4j
@Aspect
@Order(value = Ordered.HIGHEST_PRECEDENCE + 1)
public class DatabaseSelector {

    @Around("@annotation(selectDB)")
    public Object selectDB(ProceedingJoinPoint proceedingJoinPoint, SelectDB selectDB) throws Throwable {
        log.debug("Select Database - target : {}", selectDB.name());

        try {
            String databaseName = selectDB.name();
            TenantContextHolder.setCurrentTenantId(databaseName);

            return proceedingJoinPoint.proceed();
        } finally {
            TenantContextHolder.removeCurrentTenantId();
        }
    }
}
