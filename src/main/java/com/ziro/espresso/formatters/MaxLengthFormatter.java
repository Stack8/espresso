package com.ziro.espresso.formatters;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaxLengthFormatter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaxLengthFormatter.class);

    private final int maxLength;

    private MaxLengthFormatter(int maxLength) {
        Preconditions.checkArgument(maxLength > 0, "Max length must be > 0");
        this.maxLength = maxLength;
    }

    public static MaxLengthFormatter of(int maxLength) {
        return new MaxLengthFormatter(maxLength);
    }

    public String format(String value) {
        if (value.length() > maxLength) {
            LOGGER.debug("Formatting value [{}]", value);
            String truncatedValue = value.substring(0, maxLength).trim();
            LOGGER.debug("Truncated [{}] to [{}]", value, truncatedValue);
            return truncatedValue;
        }
        return value;
    }
}
