package com.ziro.espresso.okhttp3;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ziro.espresso.fluent.exceptions.SystemUnhandledException;
import java.io.IOException;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

class OAuth2ClientAccessTokens {

    private static final Cache<String, String> ACCESS_TOKENS_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(60))
            .build();
    private static final int SUCCESS_STATUS_CODE = 200;

    private OAuth2ClientAccessTokens() {
    }

    static String getAccessToken(OAuth2ClientAccessTokenRequestParameters oauth2ClientAccessTokenRequestParams) {
        try {
            return ACCESS_TOKENS_CACHE.get(
                    oauth2ClientAccessTokenRequestParams.toString(),
                    () -> accessTokensCacheLoader(oauth2ClientAccessTokenRequestParams)
            );
        } catch (ExecutionException e) {
            throw SystemUnhandledException.fluent()
                    .message(
                            "Something went wrong while trying to get access token for [%s].",
                            oauth2ClientAccessTokenRequestParams
                    )
                    .cause(e.getCause())
                    .exception();
        }
    }

    private static String accessTokensCacheLoader(
            OAuth2ClientAccessTokenRequestParameters oauth2ClientAccessTokenRequestParams
    ) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        RequestBody requestBody = new FormBody.Builder()
                .addEncoded("grant_type", "client_credentials")
                .addEncoded("scope", oauth2ClientAccessTokenRequestParams.scope())
                .addEncoded("client_id", oauth2ClientAccessTokenRequestParams.clientId())
                .addEncoded("client_secret", oauth2ClientAccessTokenRequestParams.clientSecret())
                .build();

        Request request = new Request.Builder()
                .url(oauth2ClientAccessTokenRequestParams.tokenUrl())
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "application/json")
                .post(requestBody)
                .build();

        Call call = client.newCall(request);
        try (Response response = call.execute()) {
            ResponseBody responseBody = Objects.requireNonNull(response.body(), "responseBody should not be null");
            String responseAsString = new String(responseBody.bytes());

            SystemUnhandledException.fluent()
                    .message(
                            "Failed to obtain access token from Authorization Server. "
                                    + "The Authorization Server returned [status_code=%s, response=%s].",
                            response.code(),
                            responseAsString
                    )
                    .throwIf(response.code() != SUCCESS_STATUS_CODE);

            JsonElement jwtJsonElement = JsonParser.parseString(responseAsString);
            JsonObject jwtJsonObject = jwtJsonElement.getAsJsonObject();
            return jwtJsonObject.get("access_token").getAsString();
        }
    }
}
