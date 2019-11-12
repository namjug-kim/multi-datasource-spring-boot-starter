package com.njkim.multidatabase.aspect;

import com.njkim.multidatabase.annotation.SelectDB;
import com.njkim.multidatabase.model.TenantContextHolder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

class DatabaseSelectorTest {
    @Test
    void select_annotation_to_type() {
        // GIVEN
        SelectorTypeTestClass target = new SelectorTypeTestClass();
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        DatabaseSelector aspect = new DatabaseSelector();
        factory.addAspect(aspect);
        SelectorTypeTestClass proxy = factory.getProxy();

        // WHEN
        String currentTenantId = proxy.getCurrentTenantId();

        // THEN
        Assertions.assertThat(currentTenantId
                .equalsIgnoreCase("test"));
    }

    @Test
    void select_annotation_to_method() {
        // GIVEN
        SelectorMethodTestClass target = new SelectorMethodTestClass();
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        DatabaseSelector aspect = new DatabaseSelector();
        factory.addAspect(aspect);
        SelectorMethodTestClass proxy = factory.getProxy();

        // WHEN
        String currentTenantId = proxy.getCurrentTenantId();

        // THEN
        Assertions.assertThat(currentTenantId
                .equalsIgnoreCase("test"));
    }
}

@SelectDB("test")
class SelectorTypeTestClass {
    public String getCurrentTenantId() {
        return TenantContextHolder.getCurrentTenantId();
    }
}

class SelectorMethodTestClass {
    @SelectDB("test")
    public String getCurrentTenantId() {
        return TenantContextHolder.getCurrentTenantId();
    }
}

