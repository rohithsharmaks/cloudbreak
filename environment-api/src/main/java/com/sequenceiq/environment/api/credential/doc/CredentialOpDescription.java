package com.sequenceiq.environment.api.credential.doc;

public class CredentialOpDescription {
    public static final String PUT_V1 = "modify public credential resource";
    public static final String INTERACTIVE_LOGIN = "interactive login";
    public static final String INIT_CODE_GRANT_FLOW = "start a credential creation with Oauth2 Authorization Code Grant flow";
    public static final String INIT_CODE_GRANT_FLOW_ON_EXISTING = "Reinitialize Oauth2 Authorization Code Grant flow on an existing credential";
    public static final String AUTHORIZE_CODE_GRANT_FLOW = "Authorize Oauth2 Authorization Code Grant flow";
    public static final String LIST_V1 = "list credentials";
    public static final String GET_BY_NAME_V1 = "get credential by name";
    public static final String CREATE_V1 = "create credential";
    public static final String DELETE_BY_NAME_V1 = "delete credential by name";
    public static final String DELETE_MULTIPLE_BY_NAME_V1 = "delete multiple credentials by name";
    public static final String GET_PREREQUISTIES_BY_CLOUD_PROVIDER = "get credential prerequisites for cloud platform";

    private CredentialOpDescription() {
    }
}
