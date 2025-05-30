package com.ziro.espresso.fluent.exceptions;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.ziro.espresso.javax.annotation.extensions.NonNullByDefault;
import jakarta.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Provides fluent support to any type of throwable.
 */
@NonNullByDefault
public abstract class AbstractFluentExceptionSupport<T extends Throwable> implements ExceptionDetailsStage<T> {

    private static final String FALLBACK_DEFAULT_MESSAGE = "Something went wrong.";

    private final String defaultMessage;

    @Nullable
    private String message;

    @Nullable
    private Throwable cause;

    protected AbstractFluentExceptionSupport() {
        this.defaultMessage = FALLBACK_DEFAULT_MESSAGE;
    }

    protected AbstractFluentExceptionSupport(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    // Generic factory methods - to be used by concrete exception classes
    public static <T extends Throwable> ExceptionDetailsStage<T> withCause(
            Throwable cause, Supplier<AbstractFluentExceptionSupport<T>> builderFactory) {
        AbstractFluentExceptionSupport<T> builder = builderFactory.get();
        builder.setCause(cause);
        return builder;
    }

    public static <T extends Throwable> ExceptionDetailsStage<T> asRootCause(
            Supplier<AbstractFluentExceptionSupport<T>> builderFactory) {
        return builderFactory.get();
    }

    protected Optional<String> message() {
        return Optional.ofNullable(message);
    }

    // @deprecated Use withCause(Throwable) or asRootCause() instead
    @Deprecated
    public AbstractFluentExceptionSupport<T> cause(Throwable cause) {
        this.cause = cause;
        return this;
    }

    protected Optional<Throwable> cause() {
        return Optional.ofNullable(cause);
    }

    @Override
    public ExceptionDetailsStage<T> message(String message, Object... messageArgs) {
        if (messageArgs.length > 0) {
            this.message = String.format(message, messageArgs);
        } else {
            this.message = message;
        }
        return this;
    }

    @Override
    public ExceptionDetailsStage<T> message(Supplier<String> messageSupplier) {
        this.message = messageSupplier.get();
        return this;
    }

    // Throws exception if condition not satisfied.
    // Note: IntelliJ understands that this throws an exception but may need extra null checks for null checking
    // conditions.
    @Override
    public void throwIf(boolean condition) throws T {
        if (condition) {
            throw exception();
        }
    }

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

    // The following abstract methods are the reason why we had to go abstract on this
    protected abstract T createExceptionWith(String message);

    protected abstract T createExceptionWith(String message, Throwable cause);

    // Used by the concrete exception classes
    void setCause(Throwable cause) {
        this.cause = cause;
    }
}
