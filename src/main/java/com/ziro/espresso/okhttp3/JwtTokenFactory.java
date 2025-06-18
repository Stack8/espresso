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

/**
 * Factory class for creating JWT access tokens using OAuth 2.0 client credentials flow.
 * This class handles the token request and response processing for obtaining access tokens
 * from an OAuth 2.0 authorization server.
 *
 * <p>The factory uses OkHttp client for HTTP communication and expects JSON responses
 * from the authorization server.
 *
 * <p>Example usage:
 * <pre>{@code
 * String token = JwtTokenFactory.createAccessToken(
 *     "read write",
 *     "client123",
 *     "secret456",
 *     "https://auth-server.com/oauth/token"
 * );
 * }</pre>
 */
public class JwtTokenFactory {

    private static final int SUCCESS_STATUS_CODE = 200;

    /**
     * Creates an access token by making an OAuth 2.0 client credentials grant request
     * to the specified authorization server.
     *
     * <p>The method performs the following steps:
     * <ol>
     *   <li>Constructs an OAuth 2.0 token request with client credentials
     *   <li>Sends the request to the authorization server
     *   <li>Validates the response status code
     *   <li>Parses the JSON response to extract the access token
     * </ol>
     *
     * @param scope The OAuth 2.0 scope(s) being requested. Multiple scopes should be
     *             space-delimited
     * @param clientId The client identifier issued to the client by the authorization
     *                server
     * @param clientSecret The client secret issued to the client by the authorization
     *                    server
     * @param tokenUrl The token endpoint URL of the authorization server
     * @return The JWT access token string
     * @throws IOException If there is an error in the network communication
     * @throws SystemUnhandledException If the response status code is not 200 or if
     *         the response cannot be parsed properly
     * @throws NullPointerException If the response body is null
     */
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

            SystemUnhandledException.asRootCause()
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
