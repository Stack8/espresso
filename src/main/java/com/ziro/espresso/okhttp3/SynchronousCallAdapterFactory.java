package com.ziro.espresso.okhttp3;

import com.ziro.espresso.annotations.NonNullByDefault;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A Retrofit {@link CallAdapter.Factory} that provides synchronous execution of HTTP requests.
 * This factory creates adapters that execute requests immediately on the calling thread,
 * rather than asynchronously.
 *
 * <p>The factory supports three types of return values:
 * <ul>
 *   <li>{@code Call<T>} - Delegated to Retrofit's default adapter
 *   <li>{@code Response<T>} - Returns the full HTTP response
 *   <li>{@code T} - Returns just the response body, throwing an exception for non-successful responses
 * </ul>
 *
 * <p>Example usage with Retrofit:
 * <pre>{@code
 * Retrofit retrofit = new Retrofit.Builder()
 *     .baseUrl("https://api.example.com")
 *     .addCallAdapterFactory(new SynchronousCallAdapterFactory<>())
 *     .build();
 *
 * // Interface methods can now return direct types instead of Call<T>
 * interface MyApi {
 *     User getUser(String id);           // Returns just the response body
 *     Response<User> getUserResponse(String id);  // Returns the full response
 * }
 * }</pre>
 *
 * @param <R> The response body type
 * @param <T> The adapted return type
 */
@NonNullByDefault
@Slf4j
public class SynchronousCallAdapterFactory<R, T> extends CallAdapter.Factory {
    /**
     * Creates a call adapter for the given return type.
     *
     * <p>The adapter's behavior depends on the return type:
     * <ul>
     *   <li>For {@code Call<T>}, returns null to delegate to Retrofit's default adapter
     *   <li>For {@code Response<T>}, returns an adapter that provides the full response
     *   <li>For direct types, returns an adapter that provides the response body or throws
     *       an exception for non-successful responses
     * </ul>
     *
     * @param returnType The return type of the service method
     * @param annotations The method annotations
     * @param retrofit The Retrofit instance
     * @return A call adapter for the given return type, or null if the type should be
     *         handled by Retrofit's default adapter
     */
    @Override
    public CallAdapter<R, T> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {

        // public Call<FooJson> foo();
        // retrofit2.DefaultCallAdapterFactory handles this
        if (getRawType(returnType).equals(Call.class)) {
            return null;
        }

        if (getRawType(returnType).equals(Response.class)) {
            //noinspection AnonymousInnerClass
            return new CallAdapter<>() {
                @Override
                public Type responseType() {
                    return getParameterUpperBound(0, (ParameterizedType) returnType);
                }

                @Override
                public T adapt(Call<R> call) {
                    Response<R> response;
                    try {
                        response = call.execute();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    //noinspection unchecked
                    return (T) response;
                }
            };
        }

        //noinspection AnonymousInnerClass
        return new CallAdapter<>() {
            @Override
            public Type responseType() {
                return returnType;
            }

            @Override
            public T adapt(Call<R> call) {
                Response<R> response;
                try {
                    response = call.execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                if (!response.isSuccessful()) {
                    String errorBody;
                    try {
                        //noinspection resource,ConstantConditions
                        errorBody = new String(response.errorBody().bytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    String errorMessage = String.format("Unsuccessful status %s: %s", response.code(), errorBody);
                    log.debug(errorMessage);
                    // This doesn't throw a specific exception type since the caller can return a Response<FooJson>
                    // type and inspect the response directly if they are interested in the status code.
                    throw new RuntimeException(errorMessage);
                }

                //noinspection unchecked
                return (T) response.body();
            }
        };
    }
}
