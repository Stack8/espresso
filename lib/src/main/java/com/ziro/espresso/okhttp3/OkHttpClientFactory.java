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

public class OkHttpClientFactory {

    private OkHttpClientFactory() {
    }

    public static Interceptor buildBasicAuthInterceptor(String username, String password) {
        return chain -> {
            Request request = newRequestBuilder(chain.request())
                    .addHeader("Authorization", Credentials.basic(username, password))
                    .build();
            return chain.proceed(request);
        };
    }

    public static Interceptor buildOAuth2Interceptor(
            OAuth2ClientAccessTokenRequestParameters oauth2ClientAccessTokenRequestParams
    ) {
        return chain -> {
            String accessToken = OAuth2ClientAccessTokens.getAccessToken(oauth2ClientAccessTokenRequestParams);
            Request request = newRequestBuilder(chain.request())
                    .addHeader("Authorization", String.format("Bearer %s", accessToken))
                    .build();
            return chain.proceed(request);
        };
    }

    private static Request.Builder newRequestBuilder(Request request) {
        // The request will be a path like "/admin/dial-plan-group-management",
        // we want "/services/admin/dial-plan-group-management"
        List<String> newPathSegments = new ArrayList<>(request.url().encodedPathSegments());
        newPathSegments.add(0, "services");
        return request.newBuilder()
                .url(request.url().newBuilder().encodedPath("/" + Joiner.on("/").join(newPathSegments)).build())
                // In order to be able to download files, we need to accept octet-stream.
                // As for uploading, that is handled in the individual request itself.
                .addHeader("Accept", "application/json, application/octet-stream")
                .addHeader("Content-Type", "application/json");
    }

    public static SSLSocketFactory buildSocketFactory(X509TrustManager trustManager) {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[] {trustManager};
            final SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw SystemUnhandledException.fluent()
                    .message("Something went wrong while trying to initialize all-trusting socket factory.")
                    .cause(e)
                    .exception();
        }
    }

    public static X509TrustManager createNaiveX509TrustManager() {
        return new NaiveX509TrustManager();
    }

    private static class NaiveX509TrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
            // Do nothing - trust all clients
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
            // Do nothing - trust all servers
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[] {};
        }
    }
}
