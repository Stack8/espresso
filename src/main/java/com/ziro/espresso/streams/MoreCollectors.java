package com.ziro.espresso.streams;

import com.google.common.base.Preconditions;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Utility class providing specialized collectors for Java Stream API operations
 * that enforce cardinality constraints on stream elements.
 *
 * <p>This class contains collectors that are particularly useful when working with
 * streams where you need to ensure specific collection requirements, such as
 * exactly one element or at most one element.
 */
public final class MoreCollectors {

    private MoreCollectors() {}

    /**
     * Creates a collector that enforces the presence of exactly one element in the stream.
     *
     * <p>This collector is useful in scenarios where you expect a stream to contain
     * precisely one element, and want to fail fast if this constraint is violated.
     *
     * <pre>{@code
     * String result = stream.collect(MoreCollectors.exactlyOne("user"));
     * }</pre>
     *
     * @param description A descriptive string used in the exception message to identify
     *                   the type of element being collected
     * @param <T> The type of elements in the stream
     * @return A collector that yields the single element from the stream
     * @throws IllegalStateException if the stream contains zero elements or more than
     *         one element, with a message including the provided description
     */
    public static <T> Collector<T, Object, T> exactlyOne(String description) {
        return Collectors.collectingAndThen(Collectors.toList(), list -> {
            Preconditions.checkState(
                    list.size() == 1,
                    "Expected exactly [1] '%s' match but got [%s].".formatted(description, list.size()));
            return list.get(0);
        });
    }

    /**
     * Creates a collector that ensures a stream contains at most one element.
     *
     * <p>This collector provides a more strict alternative to {@code findFirst()}
     * by explicitly validating that no more than one element exists in the stream.
     * While {@code findFirst()} silently ignores additional elements, this collector
     * will fail if multiple elements are present.
     *
     * <pre>{@code
     * Optional<String> result = stream.collect(MoreCollectors.atMostOne("user"));
     * }</pre>
     *
     * @param description A descriptive string used in the exception message to identify
     *                   the type of element being collected
     * @param <T> The type of elements in the stream
     * @return An {@link Optional} containing the element if present, or empty if
     *         the stream was empty
     * @throws IllegalStateException if the stream contains more than one element,
     *         with a message including the provided description
     */
    public static <T> Collector<T, Object, Optional<T>> atMostOne(String description) {
        return Collectors.collectingAndThen(Collectors.toList(), list -> {
            Preconditions.checkState(
                    list.size() <= 1, "Expected at most [1] '%s' but got [%s].".formatted(description, list.size()));
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }
}
