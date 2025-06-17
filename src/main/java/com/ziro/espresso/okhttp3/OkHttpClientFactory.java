package com.ziro.espresso.okhttp3;

import com.google.common.base.Joiner;
import com.ziro.espresso.fluent.exceptions.SystemUnhandledException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * Factory class for creating and configuring OkHttp clients and their components.
 * Provides utility methods for building authentication interceptors, SSL/TLS configurations,
 * and trust managers for HTTP clients.
 *
 * <p>This factory includes support for:
 * <ul>
 *   <li>Basic Authentication
 *   <li>OAuth 2.0 Bearer token authentication
 *   <li>Custom SSL/TLS configurations
 *   <li>Path prefix handling (/services/)
 * </ul>
 */
public class OkHttpClientFactory {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private OkHttpClientFactory() {}

    /**
     * Creates an interceptor that adds Basic Authentication headers to requests.
     *
     * @param username the username for Basic Authentication
     * @param password the password for Basic Authentication
     * @return an Interceptor that adds Basic Authentication headers
     */
    public static Interceptor buildBasicAuthInterceptor(String username, String password) {
        return chain -> {
            Request request = newRequestBuilder(chain.request())
                    .addHeader("Authorization", Credentials.basic(username, password))
                    .build();
            return chain.proceed(request);
        };
    }

    /**
     * Creates an interceptor that adds OAuth 2.0 Bearer token authentication headers to requests.
     * The token is automatically retrieved and refreshed using the provided parameters.
     *
     * @param oauth2ClientAccessTokenRequestParams parameters for OAuth 2.0 token requests
     * @return an Interceptor that adds OAuth 2.0 Bearer token headers
     */
    public static Interceptor buildOAuth2Interceptor(
            OAuth2ClientAccessTokenRequestParameters oauth2ClientAccessTokenRequestParams) {
        return chain -> {
            String accessToken = OAuth2ClientAccessTokens.getAccessToken(oauth2ClientAccessTokenRequestParams);
            Request request = newRequestBuilder(chain.request())
                    .addHeader("Authorization", String.format("Bearer %s", accessToken))
                    .build();
            return chain.proceed(request);
        };
    }

    /**
     * Creates an SSL socket factory with the specified trust manager.
     * The factory is configured to use TLS v1.3.
     *
     * @param trustManager the X509TrustManager to use for SSL/TLS connections
     * @return a configured SSLSocketFactory
     * @throws SystemUnhandledException if the socket factory cannot be initialized
     */
    public static SSLSocketFactory buildSocketFactory(X509TrustManager trustManager) {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[] {trustManager};
            final SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw SystemUnhandledException.withCause(e)
                    .message("Something went wrong while trying to initialize all-trusting socket factory.")
                    .exception();
        }
    }

    /**
     * Creates a trust manager that accepts all certificates.
     * 
     * @deprecated Use {@link #createNaiveX509TrustManager()} instead
     * @return an X509TrustManager that accepts all certificates
     */
    @Deprecated
    public static X509TrustManager buildX509TrustManager() {
        return new NaiveX509TrustManager();
    }

    /**
     * Creates a trust manager that accepts all certificates.
     * 
     * <p>WARNING: This trust manager is intended for development or controlled
     * environments only. It does not perform any certificate validation.
     *
     * @return an X509TrustManager that accepts all certificates
     */
    public static X509TrustManager createNaiveX509TrustManager() {
        return new NaiveX509TrustManager();
    }

    /**
     * Modifies the request builder to add the /services/ prefix and default headers.
     *
     * @param request the original request to modify
     * @return a new Request.Builder with modified path and default headers
     */
    private static Request.Builder newRequestBuilder(Request request) {
        // The request will be a path like "/admin/dial-plan-group-management",
        // we want "/services/admin/dial-plan-group-management"
        List<String> newPathSegments = new ArrayList<>(request.url().encodedPathSegments());
        newPathSegments.add(0, "services");
        return request.newBuilder()
                .url(request.url()
                        .newBuilder()
                        .encodedPath("/" + Joiner.on("/").join(newPathSegments))
                        .build())
                // In order to be able to download files, we need to accept octet-stream.
                // As for uploading, that is handled in the individual request itself.
                .addHeader("Accept", "application/json, application/octet-stream")
                .addHeader("Content-Type", "application/json");
    }

    /**
     * A trust manager implementation that accepts all certificates without validation.
     * 
     * <p>WARNING: This implementation is intended for development or controlled
     * environments only. It does not perform any certificate validation and should
     * not be used in production environments where security is a concern.
     */
    private static class NaiveX509TrustManager implements X509TrustManager {

        /**
         * Accepts all client certificates without validation.
         */
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
            // We trust any client/server for now since we deploy in our own OS,
            // so that there is no need to worry about incoming or outcome transactions
        }

        /**
         * Accepts all server certificates without validation.
         */
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
            // We trust any client/server for now since we deploy in our own OS,
            // so that there is no need to worry about incoming or outcome transactions
        }

        /**
         * Returns an empty array of accepted issuers.
         *
         * @return an empty array of X509Certificates
         */
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[] {};
        }
    }
}