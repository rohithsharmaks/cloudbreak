package com.sequenceiq.cloudbreak.auth.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sequenceiq.cloudbreak.auth.altus.Crn;
import com.sequenceiq.cloudbreak.auth.altus.GrpcUmsClient;
import com.sequenceiq.cloudbreak.auth.altus.exception.UmsAuthenticationException;
import com.sequenceiq.cloudbreak.auth.security.authentication.UmsAuthenticationService;
import com.sequenceiq.cloudbreak.common.user.CloudbreakUser;

public class CachedRemoteTokenService implements AuthenticationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CachedRemoteTokenService.class);

    private final AccessTokenConverter tokenConverter = new DefaultAccessTokenConverter();

    private final GrpcUmsClient umsClient;

    private final ObjectMapper objectMapper;

    private final JwtAccessTokenConverter jwtAccessTokenConverter;

    private final UmsAuthenticationService umsAuthenticationService;

    public CachedRemoteTokenService(GrpcUmsClient umsClient) {
        this.umsClient = umsClient;
        objectMapper = new ObjectMapper();
        jwtAccessTokenConverter = new JwtAccessTokenConverter();
        umsAuthenticationService = new UmsAuthenticationService(umsClient);
        LOGGER.debug("Init RemoteTokenServices with");
    }

    @Cacheable(cacheNames = "tokenCache", key = "#accessToken")
    public Authentication loadAuthentication(String accessToken) throws AuthenticationException, InvalidTokenException {
        if (Crn.isCrn(accessToken)) {
            try {
                return getUmsAuthentication(accessToken);
            } catch (UmsAuthenticationException e) {
                LOGGER.error("Invalid CRN provided", e);
                throw new InvalidTokenException("Invalid CRN provided", e);
            } catch (RuntimeException e) {
                LOGGER.error("Cannot authenticate", e);
                throw new InvalidTokenException("Cannot authenticate, please check logs for further details!", e);
            }
        }
        return null;
    }

    private Authentication getUmsAuthentication(String crnText) {
        Crn crn = Crn.fromString(crnText);
        CloudbreakUser cloudbreakUser = umsAuthenticationService.getCloudbreakUser(crnText, null);
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("tenant", crn.getAccountId());
        tokenMap.put("crn", crnText);
        tokenMap.put("user_id", cloudbreakUser.getUserId());
        tokenMap.put("user_name", cloudbreakUser.getEmail());
        return null;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return null;
    }
}
