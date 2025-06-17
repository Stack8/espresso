package com.ziro.espresso.fluent.exceptions;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.ziro.espresso.javax.annotation.extensions.NonNullByDefault;
import jakarta.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Abstract base class providing a fluent builder pattern for creating and configuring exceptions.
 * This class implements a two-stage builder pattern for creating exceptions with customizable
 * messages and causes.
 *
 * <p>This class serves as the foundation for implementing custom exceptions with fluent APIs.
 * It provides methods for setting messages, causes, and creating exceptions in a type-safe manner.
 *
 * <p>Example usage with a concrete implementation:
 * <pre>{@code
 * // Creating an exception with a cause
 * throw MyException.withCause(originalException)
 *     .message("Failed to process: %s", id)
 *     .exception();
 *
 * // Creating a root cause exception
 * throw MyException.asRootCause()
 *     .message("Invalid configuration")
 *     .exception();
 * }</pre>
 *
 * <p>To implement a custom exception using this class:
 * <ol>
 *   <li>Create a concrete exception class
 *   <li>Create a builder class extending this class
 *   <li>Implement the required {@code createExceptionWith} methods
 *   <li>Provide static factory methods using {@code withCause} and {@code asRootCause}
 * </ol>
 *
 * @param <T> the type of exception this builder creates
 * @see ExceptionDetailsStage
 */
@NonNullByDefault
public abstract class AbstractFluentExceptionSupport<T extends Throwable> implements ExceptionDetailsStage<T> {

    private static final String FALLBACK_DEFAULT_MESSAGE = "Something went wrong.";

    private final String defaultMessage;

    @Nullable
    private String message;

    @Nullable
    private Throwable cause;

    /**
     * Creates a new exception builder with a default message of "Something went wrong."
     */
    protected AbstractFluentExceptionSupport() {
        this.defaultMessage = FALLBACK_DEFAULT_MESSAGE;
    }

    /**
     * Creates a new exception builder with the specified default message.
     * This message will be used when no specific message is provided through
     * the builder methods.
     *
     * @param defaultMessage the message to use when no specific message is set
     */
    protected AbstractFluentExceptionSupport(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    /**
     * Creates a new exception builder that wraps an existing throwable as its cause.
     * This factory method is intended to be used by concrete exception implementations
     * to provide their fluent API.
     *
     * @param <T> the type of exception to create
     * @param cause the underlying cause of the exception
     * @param builderFactory supplier for creating new builder instances
     * @return a builder stage for constructing the exception with additional details
     */
    public static <T extends Throwable> ExceptionDetailsStage<T> withCause(
            Throwable cause, Supplier<AbstractFluentExceptionSupport<T>> builderFactory) {
        AbstractFluentExceptionSupport<T> builder = builderFactory.get();
        builder.setCause(cause);
        return builder;
    }

    /**
     * Creates a new exception builder for exceptions that represent a root cause
     * (with no underlying cause).
     *
     * @param <T> the type of exception to create
     * @param builderFactory supplier for creating new builder instances
     * @return a builder stage for constructing the exception with additional details
     */
    public static <T extends Throwable> ExceptionDetailsStage<T> asRootCause(
            Supplier<AbstractFluentExceptionSupport<T>> builderFactory) {
        return builderFactory.get();
    }

    /**
     * Returns the currently set exception message.
     *
     * <p>The message is optional and may return an empty Optional if no message has been set.
     * In such cases, when the exception is created, it will use the default message specified
     * in the constructor.
     *
     * <p>This is a protected method intended for use by subclasses that need to access
     * the raw message value during exception creation.
     *
     * @return an Optional containing the current exception message, or an empty Optional if no message is set
     */
    protected Optional<String> message() {
        return Optional.ofNullable(message);
    }

    /**
     * Sets the cause for this exception.
     *
     * @deprecated Use {@link withCause(Throwable)} to create exceptions with a cause, or {@link #asRootCause()}
     * for exceptions without a cause. These methods provide a clearer and more structured approach to exception creation.
     *
     * <p>Example of preferred usage:
     * <pre>{@code
     * // Instead of:
     * MyException.fluent().cause(e).message("Failed").exception();
     *
     * // Use:
     * MyException.withCause(e).message("Failed").exception();
     * }</pre>
     *
     * @param cause the throwable that caused this exception
     * @return this builder instance for method chaining
     */
    @Deprecated
    public AbstractFluentExceptionSupport<T> cause(Throwable cause) {
        this.cause = cause;
        return this;
    }

    /**
     * Returns the currently set cause of the exception.
     *
     * <p>The cause is optional and may return an empty Optional if no cause has been set.
     * This typically occurs when the exception is created using {@link #asRootCause()}.
     *
     * <p>This is a protected method intended for use by subclasses that need to access
     * the cause during exception creation.
     *
     * @return an Optional containing the current exception cause, or an empty Optional if no cause is set
     */
    protected Optional<Throwable> cause() {
        return Optional.ofNullable(cause);
    }

    /**
     * Sets the exception message with optional formatting arguments.
     * If formatting arguments are provided, they will be applied using
     * {@link String#format(String, Object...)}.
     *
     * @param message the message template
     * @param messageArgs optional formatting arguments
     * @return this builder instance for method chaining
     */
    @Override
    public ExceptionDetailsStage<T> message(String message, Object... messageArgs) {
        if (messageArgs.length > 0) {
            this.message = String.format(message, messageArgs);
        } else {
            this.message = message;
        }
        return this;
    }

    /**
     * Sets the exception message using a supplier.
     * The supplier will be evaluated immediately to get the message.
     *
     * @param messageSupplier supplier that provides the exception message
     * @return this builder instance for method chaining
     */
    @Override
    public ExceptionDetailsStage<T> message(Supplier<String> messageSupplier) {
        this.message = messageSupplier.get();
        return this;
    }

    /**
     * Throws the configured exception if the specified condition is true.
     * This is a convenience method for conditional exception throwing.
     *
     * @param condition the condition to evaluate
     * @throws T the configured exception if the condition is true
     */
    @Override
    public void throwIf(boolean condition) throws T {
        if (condition) {
            throw exception();
        }
    }

    /**
     * Creates and returns the exception instance with all configured properties.
     * If no message was set, uses the default message.
     *
     * @return the configured exception instance
     */
    @Override
    public T exception() {
        String exceptionMessage = message().orElse(defaultMessage);
        return cause().map(theCause -> {
                    if (hasSuppressedExceptions(theCause)) {
                        String exceptionMessageWithRootCause = appendRootCauseToMessage(exceptionMessage, theCause);
                        return createExceptionWith(exceptionMessageWithRootCause, theCause);
                    }
                    return createExceptionWith(exceptionMessage, theCause);
                })
                .orElse(createExceptionWith(exceptionMessage));
    }

    private static boolean hasSuppressedExceptions(Throwable theCause) {
        Throwable[] suppressed = theCause.getSuppressed();
        // Although suppressed should never be null,
        // being null safe here saves us some headaches with unit tests
        // where we've had to mock exceptions for wtv
        // (there aren't many, but I think it will be less confusing this way).
        return suppressed != null && suppressed.length > 0;
    }

    private static String appendRootCauseToMessage(String exceptionMessage, Throwable theCause) {
        Throwable rootCause = Throwables.getRootCause(theCause);
        String rootCauseExceptionMessage = rootCause.getMessage();
        if (Strings.isNullOrEmpty(rootCauseExceptionMessage)) {
            return exceptionMessage;
        } else {
            return "%s Root cause: %s"
                    .formatted(ensureEndsWithPeriod(exceptionMessage), ensureEndsWithPeriod(rootCauseExceptionMessage));
        }
    }

    private static String ensureEndsWithPeriod(String exceptionMessage) {
        return exceptionMessage.endsWith(".") ? exceptionMessage : exceptionMessage + ".";
    }

    /**
     * Creates an exception instance with the specified message.
     * This method must be implemented by concrete subclasses to create
     * their specific exception type.
     *
     * @param message the exception message
     * @return a new exception instance
     */
    protected abstract T createExceptionWith(String message);

    /**
     * Creates an exception instance with the specified message and cause.
     * This method must be implemented by concrete subclasses to create
     * their specific exception type.
     *
     * @param message the exception message
     * @param cause the underlying cause of the exception
     * @return a new exception instance
     */
    protected abstract T createExceptionWith(String message, Throwable cause);

    // Used by the concrete exception classes
    void setCause(Throwable cause) {
        this.cause = cause;
    }
}
