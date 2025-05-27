package com.ziro.espresso.fluent.exceptions;

import com.ziro.espresso.javax.annotation.extensions.NonNullByDefault;
import jakarta.annotation.Nonnull;

@NonNullByDefault
public class SystemUnhandledException extends RuntimeException {

    public static final String DEFAULT_MESSAGE = "ZIRO encountered an error it could not recover from.";

    private SystemUnhandledException(String message) {
        super(message);
    }

    private SystemUnhandledException(String message, Throwable cause) {
        super(message, cause);
    }

    // Creates a new exception builder that will wrap the given cause.
    public static AbstractFluentExceptionSupport<SystemUnhandledException> withCause(Throwable cause) {
        return new AbstractFluentExceptionSupport<SystemUnhandledException>() {
            @Nonnull
            @Override
            protected SystemUnhandledException createExceptionWith(@Nonnull String message) {
                return new SystemUnhandledException(message, cause);
            }

            @Nonnull
            @Override
            protected SystemUnhandledException createExceptionWith(@Nonnull String message, @Nonnull Throwable cause) {
                return new SystemUnhandledException(message, cause);
            }
        };
    }

    // Creates a new exception builder that will be a root cause (no wrapped exception).
    public static AbstractFluentExceptionSupport<SystemUnhandledException> asRootCause() {
        return new AbstractFluentExceptionSupport<SystemUnhandledException>() {
            @Nonnull
            @Override
            protected SystemUnhandledException createExceptionWith(@Nonnull String message) {
                return new SystemUnhandledException(message);
            }

            @Nonnull
            @Override
            protected SystemUnhandledException createExceptionWith(@Nonnull String message, @Nonnull Throwable cause) {
                return new SystemUnhandledException(message);
            }
        };
    }

    // @deprecated Use withCause(Throwable) or asRootCause() instead
    @Deprecated
    public static AbstractFluentExceptionSupport<SystemUnhandledException> fluent() {
        return new AbstractFluentExceptionSupport<SystemUnhandledException>() {
            @Nonnull
            @Override
            protected SystemUnhandledException createExceptionWith(@Nonnull String message) {
                return new SystemUnhandledException(message);
            }

            @Nonnull
            @Override
            protected SystemUnhandledException createExceptionWith(@Nonnull String message, @Nonnull Throwable cause) {
                return new SystemUnhandledException(message, cause);
            }
        };
    }
}
