package com.sequenceiq.environment.api.ldap.doc;

public class LdapConfigOpDescription {
    public static final String POST_CONNECTION_TEST = "test that the connection could be established of an existing or new LDAP config";
    public static final String GET_REQUEST = "get request";
    public static final String LIST_BY_WORKSPACE = "list LDAP configs for the given workspace";
    public static final String GET_BY_NAME_IN_WORKSPACE = "get LDAP config by name in workspace";
    public static final String CREATE_IN_WORKSPACE = "create LDAP config in workspace";
    public static final String DELETE_BY_NAME_IN_WORKSPACE = "delete LDAP config by name in workspace";
    public static final String DELETE_MULTIPLE_BY_NAME_IN_WORKSPACE = "delete multiple LDAP configs by name in workspace";
    public static final String ATTACH_TO_ENVIRONMENTS = "attach ldap resource to environemnts";
    public static final String DETACH_FROM_ENVIRONMENTS = "detach ldap resource from environemnts";
}
