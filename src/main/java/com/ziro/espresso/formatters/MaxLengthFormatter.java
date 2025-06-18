package com.ziro.espresso.formatters;

import com.google.common.base.Preconditions;
import com.ziro.espresso.javax.annotation.extensions.NonNullByDefault;
import lombok.extern.slf4j.Slf4j;

/**
 * A utility class that formats strings by ensuring they don't exceed a specified maximum length.
 * If a string exceeds the maximum length, it will be truncated and trimmed.
 *
 * <p>This formatter is immutable and thread-safe. The maximum length is set during construction
 * and cannot be changed afterwards.
 *
 * <p>Example usage:
 * <pre>{@code
 * MaxLengthFormatter formatter = MaxLengthFormatter.of(10);
 * String result = formatter.format("This is a long text"); // Returns "This is a"
 * }</pre>
 */
@Slf4j
@NonNullByDefault
public class MaxLengthFormatter {

    private final int maxLength;

    private MaxLengthFormatter(int maxLength) {
        Preconditions.checkArgument(maxLength > 0, "Max length must be > 0");
        this.maxLength = maxLength;
    }

    /**
     * Creates a new MaxLengthFormatter with the specified maximum length.
     *
     * @param maxLength the maximum length allowed for formatted strings. Must be greater than 0.
     * @return a new MaxLengthFormatter instance
     * @throws IllegalArgumentException if maxLength is 0 or negative
     */
    public static MaxLengthFormatter of(int maxLength) {
        return new MaxLengthFormatter(maxLength);
    }

    /**
     * Formats the input string by ensuring it doesn't exceed the maximum length.
     * If the input string is longer than the maximum length, it will be truncated
     * and trimmed of trailing whitespace.
     *
     * <p>If the input string is already within the maximum length, it will be
     * returned unchanged.
     *
     * @param value the string to format
     * @return the formatted string, truncated and trimmed if necessary
     */
    public String format(String value) {
        if (value.length() > maxLength) {
            log.debug("Formatting value [{}]", value);
            String truncatedValue = value.substring(0, maxLength).trim();
            log.debug("Truncated [{}] to [{}]", value, truncatedValue);
            return truncatedValue;
        }
        return value;
    }
}
