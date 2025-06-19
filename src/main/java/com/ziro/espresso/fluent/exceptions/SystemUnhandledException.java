package com.ziro.espresso.fluent.exceptions;

import com.ziro.espresso.annotations.NonNullByDefault;
import jakarta.annotation.Nonnull;

/**
 * A runtime exception that represents unrecoverable system-level errors in the ZIRO application.
 * This exception provides a fluent builder pattern for creating detailed error messages and
 * handling exception chains.
 *
 * <p>The exception can be created either as a root cause or by wrapping another exception.
 * It supports a fluent interface for building exception instances with custom messages and
 * causes.
 *
 * <p>Example usage:
 * <pre>{@code
 * // Creating with a cause
 * throw SystemUnhandledException.withCause(originalException)
 *     .message("Failed to process request: %s", requestId)
 *     .exception();
 *
 * // Creating as root cause
 * throw SystemUnhandledException.asRootCause()
 *     .message("Configuration is invalid")
 *     .exception();
 * }</pre>
 *
 * <p>By default, if no custom message is provided, the exception uses the message:
 * "{@value #DEFAULT_MESSAGE}"
 *
 * @see ExceptionDetailsStage
 * @see AbstractFluentExceptionSupport
 */
@NonNullByDefault
public class SystemUnhandledException extends RuntimeException {

    public static final String DEFAULT_MESSAGE = "ZIRO encountered an error it could not recover from.";

    private SystemUnhandledException(String message) {
        super(message);
    }

    private SystemUnhandledException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception builder that wraps an existing throwable as its cause.
     * This method is the preferred way to create an exception instance when there is
     * an underlying cause.
     *
     * <p>Example usage:
     * <pre>{@code
     * throw SystemUnhandledException.withCause(originalException)
     *     .message("Failed to process: %s", id)
     *     .exception();
     * }</pre>
     *
     * @param cause the underlying throwable that caused this exception
     * @return a builder stage for constructing the exception with additional details
     */
    public static ExceptionDetailsStage<SystemUnhandledException> withCause(Throwable cause) {
        return AbstractFluentExceptionSupport.withCause(cause, SystemUnhandledExceptionBuilder::new);
    }

    /**
     * Creates a new exception builder for exceptions that represent a root cause
     * (with no underlying cause). Use this method when the exception is the original
     * source of the error.
     *
     * <p>Example usage:
     * <pre>{@code
     * throw SystemUnhandledException.asRootCause()
     *     .message("Invalid system configuration")
     *     .exception();
     * }</pre>
     *
     * @return a builder stage for constructing the exception with additional details
     */
    public static ExceptionDetailsStage<SystemUnhandledException> asRootCause() {
        return AbstractFluentExceptionSupport.asRootCause(SystemUnhandledExceptionBuilder::new);
    }

    /**
     * @deprecated Use {@link #withCause(Throwable)} or {@link #asRootCause()} instead
     * for more explicit exception creation.
     *
     * @return a fluent exception support instance for building the exception
     */
    @Deprecated
    public static AbstractFluentExceptionSupport<SystemUnhandledException> fluent() {
        return new SystemUnhandledExceptionBuilder();
    }

    private static class SystemUnhandledExceptionBuilder
            extends AbstractFluentExceptionSupport<SystemUnhandledException> {

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
