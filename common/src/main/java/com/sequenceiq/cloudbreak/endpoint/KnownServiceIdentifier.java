package com.sequenceiq.cloudbreak.endpoint;

/**
 * Identifiers for known services.
 */
public enum KnownServiceIdentifier {

    /**
     * A service identifier for the NGINX web server that runs on gateway nodes.
     */
    NGINX,

    /**
     * A service identifier for the Apache Knox proxy server that runs on gateway nodes.
     */
    KNOX;
}
