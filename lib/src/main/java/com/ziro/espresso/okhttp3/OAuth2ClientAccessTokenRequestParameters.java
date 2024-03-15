package com.ziro.espresso.okhttp3;

import com.google.common.base.MoreObjects;
import com.ziro.espresso.javax.annotation.extensions.NonNullByDefault;

@NonNullByDefault
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
