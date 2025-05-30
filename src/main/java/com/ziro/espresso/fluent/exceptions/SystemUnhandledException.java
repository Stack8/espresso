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
    public static ExceptionDetailsStage<SystemUnhandledException> withCause(Throwable cause) {
        return AbstractFluentExceptionSupport.withCause(cause, SystemUnhandledExceptionBuilder::new);
    }

    // Creates a new exception builder that will be a root cause (no wrapped exception).
    public static ExceptionDetailsStage<SystemUnhandledException> asRootCause() {
        return AbstractFluentExceptionSupport.asRootCause(SystemUnhandledExceptionBuilder::new);
    }

    // @deprecated Use withCause(Throwable) or asRootCause() instead
    @Deprecated
    public static AbstractFluentExceptionSupport<SystemUnhandledException> fluent() {
        return new SystemUnhandledExceptionBuilder();
    }

    private static class SystemUnhandledExceptionBuilder extends AbstractFluentExceptionSupport<SystemUnhandledException> {
        
        public SystemUnhandledExceptionBuilder() {
            super(DEFAULT_MESSAGE);
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
