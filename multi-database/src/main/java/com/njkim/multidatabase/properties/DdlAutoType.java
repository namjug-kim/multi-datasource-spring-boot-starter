package com.njkim.multidatabase.properties;

/**
 * Please describe the role of the DdlAutoType
 * <B>History:</B>
 * Created by namjug.kim on 2019-01-29
 *
 * @author namjug.kim
 * @version 0.1
 * @since 2019-01-29
 */
public enum DdlAutoType {
    NONE("none"),
    CREATE_ONLY("create-only"),
    DROP("drop"),
    CREATE("create"),
    CREATE_DROP("create-drop"),
    VALIDATE("validate"),
    UPDATE("update");

    private String properties;

    DdlAutoType(String properties) {
        this.properties = properties;
    }

    public String getProperties() {
        return properties;
    }
}
