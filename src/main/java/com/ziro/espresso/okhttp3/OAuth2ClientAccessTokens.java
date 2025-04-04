package com.ziro.espresso.okhttp3;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ziro.espresso.fluent.exceptions.SystemUnhandledException;
import java.time.Duration;
import java.util.concurrent.ExecutionException;

public class OAuth2ClientAccessTokens {

    private static final Cache<String, String> ACCESS_TOKENS_CACHE =
            CacheBuilder.newBuilder().expireAfterWrite(Duration.ofMinutes(60)).build();

    private OAuth2ClientAccessTokens() {}

    static String getAccessToken(OAuth2ClientAccessTokenRequestParameters oauth2ClientAccessTokenRequestParams) {
        try {
            return ACCESS_TOKENS_CACHE.get(
                    oauth2ClientAccessTokenRequestParams.toString(),
                    () -> JwtTokenFactory.createAccessToken(
                            oauth2ClientAccessTokenRequestParams.scope(),
                            oauth2ClientAccessTokenRequestParams.clientId(),
                            oauth2ClientAccessTokenRequestParams.clientSecret(),
                            oauth2ClientAccessTokenRequestParams.tokenUrl()));
        } catch (ExecutionException e) {
            throw SystemUnhandledException.fluent()
                    .message(
                            "Something went wrong while trying to get access token for [%s].",
                            oauth2ClientAccessTokenRequestParams)
                    .cause(e.getCause())
                    .exception();
        }
    }
}
