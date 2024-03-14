package com.ziro.espresso.fluent.exceptions;

import com.ziro.espresso.javax.annotation.extensions.NonNullByDefault;
import javax.annotation.Nonnull;

@NonNullByDefault
public class SystemUnhandledException extends RuntimeException {

    public static final String DEFAULT_MESSAGE = "ZIRO encountered an error it could not recover from.";

    private SystemUnhandledException(String message) {
        super(message);
    }

    private SystemUnhandledException(String message, Throwable cause) {
        super(message, cause);
    }

    public static AbstractFluentExceptionSupport<SystemUnhandledException> fluent() {
        return new Fluent();
    }

    private static class Fluent extends AbstractFluentExceptionSupport<SystemUnhandledException> {

        private Fluent() {
            message(DEFAULT_MESSAGE);
        }

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
    }
}
