package com.sequenceiq.it.cloudbreak.newway;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.jersey.client.proxy.WebResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sequenceiq.cloudbreak.api.CoreApi;
import com.sequenceiq.cloudbreak.api.endpoint.v4.autoscales.AutoscaleV4Endpoint;
import com.sequenceiq.cloudbreak.client.AccessToken;
import com.sequenceiq.cloudbreak.client.ConfigKey;
import com.sequenceiq.cloudbreak.client.IdentityClient;
import com.sequenceiq.environment.api.info.endpoint.EnvironmentInfoV1Endpoint;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

public class ProxyEnvironmentClient extends com.sequenceiq.environment.client.EnvironmentClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyEnvironmentClient.class);

    private static final double TOKEN_EXPIRATION_FACTOR = 0.9;

    private final IdentityClient identityClient;

    private final ExpiringMap<String, String> identityTokenCache;

    private final String clientSecret;

    private EnvironmentInfoV1Endpoint environmentInfoV1Endpoint;

    public ProxyEnvironmentClient(String environmentAddress, String caasProtocol, String caasAddress, String refreshToken,
                                  ConfigKey configKey, String identityUrl, String clientId, String clientSecret) {
        super(environmentAddress, caasProtocol, caasAddress, refreshToken, configKey);
        this.clientSecret = clientSecret;
        identityClient = new IdentityClient(identityUrl, clientId, configKey);
        identityTokenCache = configTokenCache();
    }

    public ProxyEnvironmentClient(String environmentAddress, String caasProtocol, String caasAddress, String refreshToken, ConfigKey configKey) {
        super(environmentAddress, caasProtocol, caasAddress, refreshToken, configKey);
        identityClient = null;
        identityTokenCache = configTokenCache();
        clientSecret = null;
    }

    @SuppressWarnings("unchecked")
    private EnvironmentInfoV1Endpoint refreshIdentityTokenIfNeededAndGet() {
        String token = identityTokenCache.get(TOKEN_KEY);
        if (token == null || environmentInfoV1Endpoint == null) {
            AccessToken accessToken = identityClient.getToken(clientSecret);
            token = accessToken.getToken();
            int exp = (int) (accessToken.getExpiresIn() * TOKEN_EXPIRATION_FACTOR);
            LOGGER.info("Identity token has been renewed and expires in {} seconds", exp);
            identityTokenCache.put(TOKEN_KEY, accessToken.getToken(), ExpirationPolicy.CREATED, exp, TimeUnit.SECONDS);
            environmentInfoV1Endpoint = refreshIdentityToken(token);
        }
        return environmentInfoV1Endpoint;
    }

    @Override
    public EnvironmentInfoV1Endpoint environmentInfoV1Endpoint() {
        return refreshIdentityTokenIfNeededAndGet();
    }

    private EnvironmentInfoV1Endpoint refreshIdentityToken(String token) {
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", "Bearer " + token);
        setWebTarget(getClient().target(getEnvironmentAddress()).path(CoreApi.API_ROOT_CONTEXT));
        return WebResourceFactory.newResource(AutoscaleV4Endpoint.class, getWebTarget(), false, headers, Collections.emptyList(), EMPTY_FORM);
    }

    @Override
    protected <E> E getEndpoint(Class<E> clazz) {
        return createProxy(super.getEndpoint(clazz), clazz);
    }

    @SuppressWarnings("unchecked")
    private <I> I createProxy(I obj, Class<I> clazz) {
        return new ProxyInstanceCreator(new ProxyHandler<>(obj, new BeforeAfterMessagingProxyExecutor())).createProxy(clazz);
    }

    private ExpiringMap<String, String> configTokenCache() {
        return ExpiringMap.builder().variableExpiration().expirationPolicy(ExpirationPolicy.CREATED).build();
    }

}
