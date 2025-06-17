package com.ziro.espresso.onepasssdk;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ziro.espresso.fluent.exceptions.SystemUnhandledException;
import com.ziro.espresso.okhttp3.OkHttpClientFactory;
import com.ziro.espresso.okhttp3.SynchronousCallAdapterFactory;
import com.ziro.espresso.streams.MoreCollectors;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import lombok.Builder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A connector for interacting with 1Password Connect Server API.
 * Provides functionality to securely retrieve and parse items from 1Password vaults.
 *
 * <p>This connector manages secure connections to the 1Password Connect Server with configurable
 * SSL/TLS settings and authentication. It uses default timeout values for connection (3s),
 * read (30s), and write (30s) operations.
 *
 * <p>Example usage:
 * <pre>{@code
 * OnePasswordConnector connector = OnePasswordConnector.builder()
 *     .baseUrl("https://connect.example.com")
 *     .accessToken("your-access-token")
 *     .build();
 *
 * Properties props = connector.getSecureNoteAsProperties("vault-id", "item-id");
 * }</pre>
 */
public class OnePasswordConnector {

    private static final Duration DEFAULT_CONNECTION_TIMEOUT = Duration.ofSeconds(3);
    private static final Duration DEFAULT_READ_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration DEFAULT_WRITE_TIMEOUT = Duration.ofSeconds(30);
    private final OnePasswordConnectServerApiClient client;

    /**
     * Creates a new OnePasswordConnector instance with the specified configuration.
     *
     * @param baseUrl The base URL of the 1Password Connect Server
     * @param accessToken The access token for authentication with the 1Password Connect Server
     * @param trustManager Optional custom X509TrustManager for SSL/TLS configuration.
     *                    If null, a naive trust manager will be used
     * @throws IllegalArgumentException if baseUrl or accessToken is null or empty
     */

    @Builder
    OnePasswordConnector(String baseUrl, String accessToken, @Nullable X509TrustManager trustManager) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(baseUrl), "baseUrl is required.");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(accessToken), "accessToken is required.");
        client = createClient(
                baseUrl,
                accessToken,
                Objects.requireNonNullElseGet(trustManager, OkHttpClientFactory::createNaiveX509TrustManager));
    }

    /**
     * Retrieves a secure note from 1Password and parses it as Java properties.
     * The secure note must contain content in valid Java properties format.
     *
     * <p>The method expects the note to be stored in the 'notesPlain' field of the item.
     * The content of the note will be parsed according to the Java Properties format
     * specification.
     *
     * @param vaultId The ID of the vault containing the secure note
     * @param itemId The ID of the item containing the secure note
     * @return A Properties object containing the parsed content of the secure note
     * @throws SystemUnhandledException if:
     *         <ul>
     *             <li>The item cannot be retrieved from 1Password
     *             <li>The item does not contain a 'notesPlain' field
     *             <li>The content cannot be parsed as valid Java properties
     *         </ul>
     */
    public Properties getSecureNoteAsProperties(String vaultId, String itemId) {
        Item item = client.getItem(vaultId, itemId);
        Field notesPlain = item.fields().stream()
                .filter(field -> "notesPlain".equals(field.label()))
                .collect(MoreCollectors.exactlyOne("field with [label=notesPlain]"));
        Properties properties = new Properties();
        InputStream in = new ByteArrayInputStream(notesPlain.value().getBytes());
        try {
            properties.load(in);
        } catch (IOException e) {
            throw SystemUnhandledException.withCause(e)
                    .message(
                            "Something went wrong while trying to load secure note as properties using "
                                    + "[vaultId=%s, itemId=%s].",
                            vaultId, itemId)
                    .exception();
        }
        return properties;
    }

    /**
     * Creates and configures a 1Password Connect Server API client with the specified parameters.
     *
     * @param baseUrl The base URL of the 1Password Connect Server
     * @param accessToken The access token for authentication
     * @param trustManager The trust manager for SSL/TLS configuration
     * @return A configured OnePasswordConnectServerApiClient instance
     */
    private static OnePasswordConnectServerApiClient createClient(
            String baseUrl, String accessToken, X509TrustManager trustManager) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                .setPrettyPrinting()
                .create();

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(new SynchronousCallAdapterFactory<>());

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder
                .connectTimeout(DEFAULT_CONNECTION_TIMEOUT.getSeconds(), TimeUnit.SECONDS)
                .readTimeout(DEFAULT_READ_TIMEOUT.getSeconds(), TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT.getSeconds(), TimeUnit.SECONDS)
                .hostnameVerifier((s, sslSession) -> s.equalsIgnoreCase(sslSession.getPeerHost()));

        SSLSocketFactory sslSocketFactory = OkHttpClientFactory.buildSocketFactory(trustManager);
        okHttpClientBuilder.sslSocketFactory(sslSocketFactory, trustManager);
        okHttpClientBuilder.addInterceptor(chain -> chain.proceed(addRequiredHeaders(chain.request(), accessToken)));
        OkHttpClient okHttpClient = okHttpClientBuilder.build();
        Retrofit retrofit = retrofitBuilder.client(okHttpClient).build();
        return retrofit.create(OnePasswordConnectServerApiClient.class);
    }

    /**
     * Adds required HTTP headers to the request for 1Password Connect Server API authentication.
     *
     * @param request The original request
     * @param accessToken The access token to be included in the Authorization header
     * @return A new request with the required headers
     */
    private static Request addRequiredHeaders(Request request, String accessToken) {
        return request.newBuilder()
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", String.format("Bearer %s", accessToken))
                .build();
    }
}
