package com.ziro.espresso.okhttp3;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.ziro.espresso.javax.annotation.extensions.NonNullByDefault;

@NonNullByDefault
public class OAuth2ClientAccessTokenRequestParameters {

    private final String tokenUrl;
    private final String clientId;
    private final String clientSecret;
    private final String scope;

    public OAuth2ClientAccessTokenRequestParameters(
            String tokenUrl, String clientId, String clientSecret, String scope) {
        this.tokenUrl = tokenUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.scope = scope;
    }

    public String getTokenUrl() {
        return tokenUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getScope() {
        return scope;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OAuth2ClientAccessTokenRequestParameters that = (OAuth2ClientAccessTokenRequestParameters) o;
        return Objects.equal(tokenUrl, that.tokenUrl)
                && Objects.equal(clientId, that.clientId)
                && Objects.equal(clientSecret, that.clientSecret)
                && Objects.equal(scope, that.scope);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tokenUrl, clientId, clientSecret, scope);
    }

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
