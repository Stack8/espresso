package com.ziro.espresso.okhttp3;

import com.google.common.base.MoreObjects;

/**
 * Record class representing parameters required for OAuth 2.0 client credentials flow token request.
 *
 * @param tokenUrl The URL endpoint for obtaining the access token
 * @param clientId The client identifier issued to the client by the authorization server
 * @param clientSecret The client secret issued to the client by the authorization server
 * @param scope The scope of the access request
 */
public record OAuth2ClientAccessTokenRequestParameters(
        String tokenUrl, String clientId, String clientSecret, String scope) {

    @Override
    public String toString() {
        // Intentionally excluded secret
        return MoreObjects.toStringHelper(this)
                .add("tokenUrl", tokenUrl)
                .add("clientId", clientId)
                .add("scope", scope)
                .toString();
    }
}
