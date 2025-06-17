package com.ziro.espresso.okhttp3;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ziro.espresso.fluent.exceptions.SystemUnhandledException;
import java.time.Duration;
import java.util.concurrent.ExecutionException;

/**
 * Manages OAuth 2.0 client access tokens with caching capabilities.
 * This utility class provides centralized access token management with automatic
 * token caching and expiration handling.
 *
 * <p>Access tokens are cached for 60 minutes from their creation time to minimize
 * the number of token requests to the authorization server. The cache automatically
 * evicts expired tokens.
 *
 * <p>The class uses Guava's Cache implementation for token storage and implements
 * a thread-safe approach to token management.
 */
public class OAuth2ClientAccessTokens {

    /**
     * Cache for storing access tokens with a 60-minute expiration policy.
     * The key is the string representation of OAuth2ClientAccessTokenRequestParameters,
     * and the value is the corresponding access token.
     */
    private static final Cache<String, String> ACCESS_TOKENS_CACHE =
            CacheBuilder.newBuilder().expireAfterWrite(Duration.ofMinutes(60)).build();

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private OAuth2ClientAccessTokens() {}

    /**
     * Retrieves an OAuth 2.0 access token for the specified parameters, using cached
     * tokens when available and valid.
     *
     * <p>This method first checks the cache for an existing valid token. If no valid
     * token is found, it requests a new one using the {@link JwtTokenFactory} and
     * caches it for future use.
     *
     * <p>The method is thread-safe and handles concurrent requests efficiently through
     * the underlying cache implementation.
     *
     * @param oauth2ClientAccessTokenRequestParams The parameters required for obtaining
     *                                            an access token, including client credentials,
     *                                            scope, and token endpoint URL
     * @return The access token string
     * @throws SystemUnhandledException if token acquisition fails for any reason, wrapping
     *                                 the original cause of the failure
     */
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
            throw SystemUnhandledException.withCause(e.getCause())
                    .message(
                            "Something went wrong while trying to get access token for [%s].",
                            oauth2ClientAccessTokenRequestParams)
                    .exception();
        }
    }
}
