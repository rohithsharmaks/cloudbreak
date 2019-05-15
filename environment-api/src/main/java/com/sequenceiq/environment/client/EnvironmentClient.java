package com.sequenceiq.environment.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.jersey.client.proxy.WebResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sequenceiq.cloudbreak.client.CaasClient;
import com.sequenceiq.cloudbreak.client.ConfigKey;
import com.sequenceiq.cloudbreak.client.RestClientUtil;
import com.sequenceiq.cloudbreak.client.TokenRequest;
import com.sequenceiq.cloudbreak.client.TokenUnavailableException;
import com.sequenceiq.environment.api.EnvironmentApi;
import com.sequenceiq.environment.api.info.endpoint.EnvironmentInfoV1Endpoint;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

public class EnvironmentClient {

    private static final Form EMPTY_FORM = new Form();

    protected static final String TOKEN_KEY = "TOKEN";

    private final Logger logger = LoggerFactory.getLogger(EnvironmentClient.class);

    private final ExpiringMap<String, String> tokenCache;

    private final Client client;

    private final String environmentAddress;

    private String refreshToken;

    private final CaasClient caasClient;

    private WebTarget webTarget;

    private EndpointHolder endpointHolder;

    private EnvironmentClient(String environmentAddress, String caasProtocol, String caasAddress, String refreshToken, ConfigKey configKey) {
        client = RestClientUtil.get(configKey);
        this.environmentAddress = environmentAddress;
        this.refreshToken = refreshToken;
        caasClient = new CaasClient(caasProtocol, caasAddress, configKey);
        tokenCache = configTokenCache();
        logger.info("EnvironmentClient has been created with token. environment: {}, token: {}, configKey: {}", environmentAddress, refreshToken, configKey);
    }

    public EnvironmentInfoV1Endpoint environmentInfoV1Endpoint() {
        return refreshIfNeededAndGet(EnvironmentInfoV1Endpoint.class);
    }

    private ExpiringMap<String, String> configTokenCache() {
        return ExpiringMap.builder().variableExpiration().expirationPolicy(ExpirationPolicy.CREATED).build();
    }

    private synchronized <T> T refreshIfNeededAndGet(Class<T> clazz) {
        if (refreshToken != null) {
            String accessToken = tokenCache.get(TOKEN_KEY);
            if (accessToken == null || endpointHolder == null) {
                TokenRequest tokenRequest = new TokenRequest();
                tokenRequest.setRefreshToken(refreshToken);
                accessToken = caasClient.getAccessToken(tokenRequest);
                tokenCache.put(TOKEN_KEY, accessToken, ExpirationPolicy.CREATED, 1, TimeUnit.MINUTES);
                MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
                headers.add("Authorization", "Bearer " + accessToken);
                webTarget = client.target(environmentAddress).path(EnvironmentApi.API_ROOT_CONTEXT);
                endpointHolder = new EndpointHolder(newEndpoint(EnvironmentInfoV1Endpoint.class, headers));
                logger.info("Endpoints have been renewed for EnvironmentClient");
            }
            return (T) endpointHolder.endpoints.stream().filter(e -> e.getClass().equals(clazz)).findFirst().get();
        }
        throw new TokenUnavailableException("No Refresh token provided for EnvironmentClient!");
    }

    private <C> C newEndpoint(Class<C> resourceInterface, MultivaluedMap<String, Object> headers) {
        return WebResourceFactory.newResource(resourceInterface, webTarget, false, headers, Collections.emptyList(), EMPTY_FORM);
    }

    private static class EndpointHolder {
        private final List<?> endpoints;

        EndpointHolder(Object... endpoints) {
            this.endpoints = Arrays.asList(endpoints);
        }
    }

    protected Client getClient() {
        return client;
    }

    protected void setWebTarget(WebTarget webTarget) {
        this.webTarget = webTarget;
    }

    protected WebTarget getWebTarget() {
        return webTarget;
    }

    protected String getEnvironmentAddress() {
        return environmentAddress;
    }

    protected <E> E getEndpoint(Class<E> clazz) {
        return refreshIfNeededAndGet(clazz);
    }

    public static class EnvironmentClientBuilder {

        private final String environmentAddress;

        private String refreshToken;

        private String caasProtocol;

        private String caasAddress;

        private boolean debug;

        private boolean secure = true;

        private boolean ignorePreValidation;

        public EnvironmentClientBuilder(String environmentAddress, String caasProtocol, String caasAddress) {
            this.environmentAddress = environmentAddress;
            this.caasProtocol = caasProtocol;
            this.caasAddress = caasAddress;
        }

        public EnvironmentClientBuilder withCredential(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public EnvironmentClientBuilder withDebug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public EnvironmentClientBuilder withCertificateValidation(boolean secure) {
            this.secure = secure;
            return this;
        }

        public EnvironmentClientBuilder withIgnorePreValidation(boolean ignorePreValidation) {
            this.ignorePreValidation = ignorePreValidation;
            return this;
        }

        public EnvironmentClient build() {
            ConfigKey configKey = new ConfigKey(secure, debug, ignorePreValidation);
            return new EnvironmentClient(environmentAddress, caasProtocol, caasAddress, refreshToken, configKey);
        }
    }
}
