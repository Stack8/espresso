package com.ziro.espresso.streams;

import com.google.common.base.Preconditions;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class MoreCollectors {

    private MoreCollectors() {
    }

    /**
     * Collects to exactly one element, throwing an IllegalStateException if there are either 0 or more than 1 elements.
     */
    public static <T> Collector<T, Object, T> exactlyOne(String description, Object... descriptionParams) {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    Preconditions.checkState(
                            list.size() == 1,
                            String.format(
                                    "Expected exactly [1] '%s' match but got [%s].",
                                    String.format(description, descriptionParams),
                                    list.size()
                            )
                    );
                    return list.get(0);
                }
        );
    }

    /**
     * Collects to at most one element. Although this can be done with list.stream().findFirst using this collector
     * will throw an IllegalStateException if there is more than one element.
     */
    public static <T> Collector<T, Object, Optional<T>> atMostOne(String description, Object... descriptionParams) {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    Preconditions.checkState(
                            list.size() <= 1,
                            String.format(
                                    "Expected at most [1] '%s' but got [%s].",
                                    String.format(description, descriptionParams),
                                    list.size()
                            )
                    );
                    return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
                }
        );
    }
}
