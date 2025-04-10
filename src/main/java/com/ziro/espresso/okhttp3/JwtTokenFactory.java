package com.ziro.espresso.okhttp3;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ziro.espresso.fluent.exceptions.SystemUnhandledException;
import java.io.IOException;
import java.util.Objects;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class JwtTokenFactory {

    private static final int SUCCESS_STATUS_CODE = 200;

    public static String createAccessToken(String scope, String clientId, String clientSecret, String tokenUrl)
            throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        RequestBody requestBody = new FormBody.Builder()
                .addEncoded("grant_type", "client_credentials")
                .addEncoded("scope", scope)
                .addEncoded("client_id", clientId)
                .addEncoded("client_secret", clientSecret)
                .build();

        Request request = new Request.Builder()
                .url(tokenUrl)
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
                            response.code(), responseAsString)
                    .throwIf(response.code() != SUCCESS_STATUS_CODE);

            JsonParser jsonParser = new JsonParser();
            JsonElement jwtJsonElement = jsonParser.parse(responseAsString);
            JsonObject jwtJsonObject = jwtJsonElement.getAsJsonObject();
            return jwtJsonObject.get("access_token").getAsString();
        }
    }
}
