package com.njkim.multidatabase.model;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

/**
 * Please describe the role of the CurrentTenantResolver
 * <B>History:</B>
 * Created by namjug.kim on 2018. 9. 3.
 *
 * @author namjug.kim
 * @version 0.1
 * @since 2018. 9. 3.
 */
public class CurrentTenantResolver implements CurrentTenantIdentifierResolver {

    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenantId = TenantContextHolder.getCurrentTenantId();

        if (tenantId == null) {
            tenantId = "default";
        }

        return tenantId;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }
}
