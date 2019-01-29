package com.njkim.multidatabase.model;

import org.springframework.core.NamedThreadLocal;

import java.util.Stack;

/**
 * Please describe the role of the TenantContextHolder
 * <B>History:</B>
 * Created by namjug.kim on 2018. 9. 3.
 *
 * @author namjug.kim
 * @version 0.1
 * @since 2018. 9. 3.
 */
public class TenantContextHolder {

    private static final ThreadLocal<Stack<String>> threadLocal = new NamedThreadLocal<>("current tenant holder");

    public static String getCurrentTenantId() {
        Stack<String> stack = threadLocal.get();
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        return stack.peek();
    }

    public static void setCurrentTenantId(String tenantId) {
        Stack<String> stack = threadLocal.get();
        if (stack == null) {
            stack = new Stack<>();
            threadLocal.set(stack);
        }
        stack.push(tenantId);
    }

    public static void removeCurrentTenantId() {
        Stack<String> stack = threadLocal.get();
        if (stack == null || stack.isEmpty()) {
            return;
        }
        stack.pop();
    }

    public static void releaseContext() {
        threadLocal.remove();
    }

}
