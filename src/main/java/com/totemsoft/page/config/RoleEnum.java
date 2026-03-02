package com.totemsoft.page.config;

public enum RoleEnum {

    ADMIN_PAGE,
    ADMIN_USER,
    SETUP,
    OIDC_USER
    ;

    public static final String IS_AUTHENTICATED = "isAuthenticated()";
    public static final String PERMIT_ALL = "permitAll()";
    public static final String ROLE_PREFIX = "ROLE_";

    public static final String HAS_ROLE_ADMIN_PAGE = "hasRole('ADMIN_PAGE')";
    public static final String HAS_ROLE_ADMIN_USER = "hasRole('ADMIN_USER')";
    public static final String HAS_ROLE_SETUP = "hasRole('SETUP')";
    public static final String HAS_AUTHORITY_OIDC_USER = "hasAuthority('OIDC_USER')";

    public String hasRole() {
        return "hasRole('" + name() + "')";
    }

    public String hasAuthority() {
        return "hasAuthority('" + name() + "')";
    }

}
