package com.ziro.espresso.collections;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class providing additional list operations beyond those available in the Java Collections Framework.
 * This class includes methods for performing set operations (union, intersection) on lists while maintaining
 * collection properties such as uniqueness of elements.
 *
 * <p>All operations in this class:
 * <ul>
 *     <li>Return new list instances, never modifying the input collections
 *     <li>Rely on {@code hashCode()} and {@code equals()} for element comparison
 *     <li>Do not guarantee any specific ordering of elements in the result
 *     <li>Ensure no duplicate elements in the result
 * </ul>
 *
 * <p>Example usage:
 * <pre>{@code
 * List<String> list1 = Arrays.asList("a", "b", "c");
 * List<String> list2 = Arrays.asList("b", "c", "d");
 * List<List<String>> lists = Arrays.asList(list1, list2);
 *
 * List<String> union = MoreLists.union(lists);        // [a, b, c, d]
 * List<String> intersection = MoreLists.intersection(lists);  // [b, c]
 * }</pre>
 */
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
