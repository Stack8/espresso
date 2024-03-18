package com.ziro.espresso.fluent.exceptions;

import java.util.Optional;
import javax.annotation.Nullable;

/**
 * Provides fluent support to any type of throwable.
 */
public abstract class AbstractFluentExceptionSupport<T extends Throwable> {

    private static final String DEFAULT_EXCEPTION_MESSAGE = "Something went wrong.";

    @Nullable
    private String message;

    @Nullable
    private Throwable cause;

    protected AbstractFluentExceptionSupport() {}

    public AbstractFluentExceptionSupport<T> message(String message, Object... messageArgs) {
        if (messageArgs.length > 0) {
            this.message = String.format(message, messageArgs);
        } else {
            this.message = message;
        }
        return this;
    }

    protected Optional<String> message() {
        return Optional.ofNullable(message);
    }

    public AbstractFluentExceptionSupport<T> cause(Throwable cause) {
        this.cause = cause;
        return this;
    }

    protected Optional<Throwable> cause() {
        return Optional.ofNullable(cause);
    }

    /**
     * Throws exception if condition not satisfied.
     * IntelliJ understands that this throws an exception, however, it does understand if the condition is null
     * checking. Hence, you may need extra null checks to make IntelliJ happy if your condition is null checking.
     * @throws T thrown exception.
     */
    public void throwIf(boolean condition) throws T {
        if (condition) {
            throw exception();
        }
    }

    public T exception() {
        String currentMessage = message().orElse(DEFAULT_EXCEPTION_MESSAGE);
        return cause().map(theCause -> createExceptionWith(currentMessage, theCause))
                .orElse(createExceptionWith(currentMessage));
    }

    // The following abstract methods is the reason why we had to go abstract on this
    protected abstract T createExceptionWith(String message);

    protected abstract T createExceptionWith(String message, Throwable cause);
}
