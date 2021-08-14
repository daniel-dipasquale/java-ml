package com.dipasquale.data.structure.map;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap;

public final class SortedByValueHashMapTest {
    private static final SortedByValueHashMap<String, Long> TEST = new SortedByValueHashMap<>(Long::compareTo); // TODO: make test cases for interfaces and reusable

    @BeforeEach
    public void beforeEach() {
        TEST.clear();
    }

    @Test
    public void GIVEN_a_set_of_items_inserted_in_random_order_WHEN_iterating_through_the_items_THEN_iterate_through_them_in_ascending_order_driven_by_the_item_value() {
        Assertions.assertNull(TEST.put("a", 2L));
        Assertions.assertNull(TEST.put("b", 4L));
        Assertions.assertNull(TEST.put("c", 1L));

        Assertions.assertEquals(ImmutableList.builder()
                .add(new AbstractMap.SimpleImmutableEntry<>("c", 1L))
                .add(new AbstractMap.SimpleImmutableEntry<>("a", 2L))
                .add(new AbstractMap.SimpleImmutableEntry<>("b", 4L))
                .build(), ImmutableList.copyOf(TEST::iterator));

        Assertions.assertEquals(3, TEST.size());
    }

    @Test
    public void GIVEN_a_set_of_items_WHEN_inserting_an_item_whose_key_and_value_are_already_in_the_set_THEN_do_nothing() {
        Assertions.assertNull(TEST.put("a", 1L));
        Assertions.assertEquals(Long.valueOf(1L), TEST.put("a", 1L));

        Assertions.assertEquals(ImmutableList.builder()
                .add(new AbstractMap.SimpleImmutableEntry<>("a", 1L))
                .build(), ImmutableList.copyOf(TEST::iterator));

        Assertions.assertEquals(1, TEST.size());
    }

    @Test
    public void GIVEN_a_set_of_items_WHEN_inserting_an_item_whose_key_is_already_in_the_set_but_the_value_is_different_THEN_replace_the_value_for_such_item() {
        Assertions.assertNull(TEST.put("a", 2L));
        Assertions.assertNull(TEST.put("b", 4L));
        Assertions.assertNull(TEST.put("c", 1L));
        Assertions.assertEquals(Long.valueOf(1L), TEST.put("c", 5L));

        Assertions.assertEquals(ImmutableList.builder()
                .add(new AbstractMap.SimpleImmutableEntry<>("a", 2L))
                .add(new AbstractMap.SimpleImmutableEntry<>("b", 4L))
                .add(new AbstractMap.SimpleImmutableEntry<>("c", 5L))
                .build(), ImmutableList.copyOf(TEST::iterator));

        Assertions.assertEquals(3, TEST.size());
    }

    @Test
    public void GIVEN_a_set_of_items_in_an_ordered_set_WHEN_removing_an_item_by_key_THEN_ensure_the_set_remains_sorted() {
        Assertions.assertNull(TEST.put("a", 2L));
        Assertions.assertNull(TEST.put("b", 4L));
        Assertions.assertNull(TEST.put("c", 1L));
        Assertions.assertEquals(Long.valueOf(2L), TEST.remove("a"));

        Assertions.assertEquals(ImmutableList.builder()
                .add(new AbstractMap.SimpleImmutableEntry<>("c", 1L))
                .add(new AbstractMap.SimpleImmutableEntry<>("b", 4L))
                .build(), ImmutableList.copyOf(TEST::iterator));

        Assertions.assertEquals(2, TEST.size());
    }

    @Test
    public void GIVEN_a_set_of_items_with_one_item_in_it_WHEN_removing_it_by_key_THEN_ensure_the_set_becomes_empty() {
        Assertions.assertNull(TEST.put("a", 1L));
        Assertions.assertEquals(Long.valueOf(1L), TEST.remove("a"));
        Assertions.assertEquals(ImmutableList.of(), ImmutableList.copyOf(TEST::iterator));
        Assertions.assertEquals(0, TEST.size());
    }

    @Test
    public void GIVEN_a_set_of_items_WHEN_removing_an_item_by_key_that_does_not_exist_THEN_do_nothing() {
        Assertions.assertNull(TEST.put("a", 2L));
        Assertions.assertNull(TEST.put("b", 4L));
        Assertions.assertNull(TEST.put("c", 1L));
        Assertions.assertNull(TEST.remove("d"));

        Assertions.assertEquals(ImmutableList.builder()
                .add(new AbstractMap.SimpleImmutableEntry<>("c", 1L))
                .add(new AbstractMap.SimpleImmutableEntry<>("a", 2L))
                .add(new AbstractMap.SimpleImmutableEntry<>("b", 4L))
                .build(), ImmutableList.copyOf(TEST::iterator));

        Assertions.assertEquals(3, TEST.size());
    }

    @Test
    public void GIVEN_a_set_of_items_with_the_same_value_throughout_WHEN_removing_an_item_by_key_THEN_ensure_the_single_item_is_removed() {
        Assertions.assertNull(TEST.put("a", 1L));
        Assertions.assertNull(TEST.put("b", 1L));
        Assertions.assertNull(TEST.put("c", 1L));
        Assertions.assertEquals(Long.valueOf(1L), TEST.remove("a"));

        Assertions.assertEquals(ImmutableList.builder()
                .add(new AbstractMap.SimpleImmutableEntry<>("b", 1L))
                .add(new AbstractMap.SimpleImmutableEntry<>("c", 1L))
                .build(), ImmutableList.copyOf(TEST::iterator));

        Assertions.assertEquals(2, TEST.size());
    }

    @Test
    public void GIVEN_a_set_of_items_WHEN_determining_the_head_entry_or_also_known_as_lowest_value_THEN_get_the_lowest_valued_item() {
        Assertions.assertNull(TEST.put("a", 2L));
        Assertions.assertNull(TEST.put("b", 4L));
        Assertions.assertNull(TEST.put("c", 1L));
        Assertions.assertEquals(new AbstractMap.SimpleImmutableEntry<>("c", 1L), TEST.headEntry());
        Assertions.assertEquals("c", TEST.headKey());
        Assertions.assertEquals(Long.valueOf(1L), TEST.headValue());
        Assertions.assertEquals(3, TEST.size());
    }

    @Test
    public void GIVEN_a_set_of_items_WHEN_clearing_it_THEN_ensure_the_set_becomes_empty() {
        Assertions.assertNull(TEST.put("a", 2L));
        Assertions.assertNull(TEST.put("b", 4L));
        Assertions.assertNull(TEST.put("c", 1L));
        TEST.clear();
        Assertions.assertEquals(ImmutableList.of(), ImmutableList.copyOf(TEST::iterator));
        Assertions.assertEquals(0, TEST.size());
    }

    @Test
    public void GIVEN_a_set_of_items_WHEN_retrieving_the_item_by_key_THEN_get_the_item() {
        Assertions.assertNull(TEST.put("a", 1L));
        Assertions.assertEquals(Long.valueOf(1L), TEST.get("a"));
        Assertions.assertEquals(1, TEST.size());
    }

    @Test
    public void GIVEN_a_set_of_items_WHEN_determining_whether_the_item_that_exists_in_the_set_is_indeed_in_the_set_THEN_show_the_item_exists() {
        Assertions.assertNull(TEST.put("a", 1L));
        Assertions.assertTrue(TEST.containsKey("a"));
        Assertions.assertEquals(1, TEST.size());
    }
}
