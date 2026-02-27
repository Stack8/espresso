package com.ziro.espresso.fluent.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SystemUnhandledExceptionTest {

    @Test
    void whenUsingWithCauseThenExceptionWrapsOriginalCause() {
        RuntimeException originalCause = new RuntimeException("Original error");

        SystemUnhandledException exception = SystemUnhandledException.withCause(originalCause)
                .message("Something went wrong")
                .exception();

        assertThat(exception.getMessage()).isEqualTo("Something went wrong");
        assertThat(exception.getCause()).isEqualTo(originalCause);
    }

    @Test
    void whenUsingAsRootCauseThenExceptionHasNoCause() {
        SystemUnhandledException exception = SystemUnhandledException.asRootCause()
                .message("Something went wrong")
                .exception();

        assertThat(exception.getMessage()).isEqualTo("Something went wrong");
        assertThat(exception.getCause()).isNull();
    }

    @Test
    void whenUsingMessageWithFormatting_thenFormatsCorrectly() {
        SystemUnhandledException exception = SystemUnhandledException.asRootCause()
                .message("Error processing %s with value %d", "item", 42)
                .exception();

        assertThat(exception.getMessage()).isEqualTo("Error processing item with value 42");
    }

    @Test
    void whenNoMessageProvidedThenUsesDefaultMessage() {
        SystemUnhandledException exception =
                SystemUnhandledException.asRootCause().exception();

        assertThat(exception.getMessage()).isEqualTo(SystemUnhandledException.DEFAULT_MESSAGE);
    }

    @Test
    void whenNoMessageProvidedWithCauseThenUsesDefaultMessage() {
        RuntimeException originalCause = new RuntimeException("Original error");

        SystemUnhandledException exception =
                SystemUnhandledException.withCause(originalCause).exception();

        assertThat(exception.getMessage()).isEqualTo(SystemUnhandledException.DEFAULT_MESSAGE);
        assertThat(exception.getCause()).isEqualTo(originalCause);
    }
}
