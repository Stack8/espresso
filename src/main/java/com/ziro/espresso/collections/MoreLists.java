package com.ziro.espresso.collections;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MoreLists {

    private MoreLists() {}

    /**
     * Return a list containing the union of the provided list of elements.
     *
     * <p>The returned union is guaranteed to not contain duplicate values.
     * There is no guarantee of ordering of elements.
     * The operation relies on hashCode() for comparison of objects.
     *
     * @param lists List containing the lists that we want to union.
     * @return The resulting list.
     */
    public static <E> List<E> union(List<List<E>> lists) {
        Set<E> set = new HashSet<>();
        lists.forEach(set::addAll);
        return new ArrayList<>(set);
    }

    /**
     * Return a list containing the intersection of the provided list of elements.
     *
     * <p>The returned intersection is guaranteed to not contain duplicate values.
     * There is no guarantee of ordering of elements.
     * The operation relies on hashCode() for comparison of objects.
     *
     * @param lists List containing the lists that we want to intersect.
     * @return The resulting list.
     */
    public static <E> List<E> intersection(List<List<E>> lists) {
        if (lists.isEmpty()) {
            return new ArrayList<>();
        }
        Set<E> set = new HashSet<>(lists.get(0));
        lists.forEach(set::retainAll);
        return new ArrayList<>(set);
    }
}
