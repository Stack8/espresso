package com.ziro.espresso.collections;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class MoreListsTest {

    @Test
    void union() {
        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();

        List<List<Integer>> lists = List.of(list1, list2);
        assertThat(MoreLists.union(lists)).isEmpty();

        list1.add(1);
        assertListContainsExactlyInAnyOrder(MoreLists.union(lists), 1);

        list2.add(1);
        list2.add(2);
        list2.add(2);
        list2.add(3);
        assertListContainsExactlyInAnyOrder(MoreLists.union(lists), 1, 2, 3);
        System.out.println("TROUBLESHOOOT");
        assertThat(true).isFalse();
    }

    @Test
    void intersection() {
        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();

        List<List<Integer>> lists = List.of(list1, list2);
        assertThat(MoreLists.intersection(lists)).isEmpty();

        list1.add(1);
        assertThat(MoreLists.intersection(lists)).isEmpty();

        list2.add(3);
        assertThat(MoreLists.intersection(lists)).isEmpty();

        list1.add(2);

        list2.add(1);
        list2.add(2);
        list2.add(3);
        assertListContainsExactlyInAnyOrder(MoreLists.intersection(lists), 1, 2);
    }

    private void assertListContainsExactlyInAnyOrder(List<Integer> list, Integer... expectedValues) {
        assertThat(list).containsOnly(expectedValues);
        assertThat(list).doesNotHaveDuplicates();
    }
}
