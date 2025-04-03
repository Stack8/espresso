package com.ziro.espresso.okhttp3;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SyncResponseAdapterFactory<R, T> extends CallAdapter.Factory {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncResponseAdapterFactory.class);

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
                    LOGGER.debug(errorMessage);
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
