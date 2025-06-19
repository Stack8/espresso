package com.ziro.espresso.fluent.exceptions;

import com.ziro.espresso.annotations.NonNullByDefault;
import java.util.function.Supplier;

/**
 * Second stage of the fluent exception API where message details can be set
 * and the exception can be created or thrown.
 * This stage is reached after explicitly choosing between withCause() or asRootCause().
 */
@NonNullByDefault
public interface ExceptionDetailsStage<T extends Throwable> {

    ExceptionDetailsStage<T> message(String message, Object... messageArgs);

    ExceptionDetailsStage<T> message(Supplier<String> messageSupplier);

    T exception();

    void throwIf(boolean condition) throws T;
}
