package com.dipasquale.data.structure.map;

import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.AbstractMap;

public final class HashSortedByValueMapTest {
    private static final HashSortedByValueMap<String, Long> TEST = new HashSortedByValueMap<>(Long::compareTo); // TODO: make test cases for interfaces and reusable

    @Before
    public void before() {
        TEST.clear();
    }

    @Test
    public void GIVEN_a_set_of_items_inserted_in_random_order_WHEN_iterating_through_the_items_THEN_iterate_through_them_in_ascending_order_driven_by_the_item_value() {
        Assert.assertNull(TEST.put("a", 2L));
        Assert.assertNull(TEST.put("b", 4L));
        Assert.assertNull(TEST.put("c", 1L));

        Assert.assertEquals(ImmutableList.builder()
                .add(new AbstractMap.SimpleImmutableEntry<>("c", 1L))
                .add(new AbstractMap.SimpleImmutableEntry<>("a", 2L))
                .add(new AbstractMap.SimpleImmutableEntry<>("b", 4L))
                .build(), ImmutableList.copyOf(TEST::iterator));

        Assert.assertEquals(3, TEST.size());
    }

    @Test
    public void GIVEN_a_set_of_items_WHEN_inserting_an_item_whose_key_and_value_are_already_in_the_set_THEN_do_nothing() {
        Assert.assertNull(TEST.put("a", 1L));
        Assert.assertEquals(Long.valueOf(1L), TEST.put("a", 1L));

        Assert.assertEquals(ImmutableList.builder()
                .add(new AbstractMap.SimpleImmutableEntry<>("a", 1L))
                .build(), ImmutableList.copyOf(TEST::iterator));

        Assert.assertEquals(1, TEST.size());
    }

    @Test
    public void GIVEN_a_set_of_items_WHEN_inserting_an_item_whose_key_is_already_in_the_set_but_the_value_is_different_THEN_replace_the_value_for_such_item() {
        Assert.assertNull(TEST.put("a", 2L));
        Assert.assertNull(TEST.put("b", 4L));
        Assert.assertNull(TEST.put("c", 1L));
        Assert.assertEquals(Long.valueOf(1L), TEST.put("c", 5L));

        Assert.assertEquals(ImmutableList.builder()
                .add(new AbstractMap.SimpleImmutableEntry<>("a", 2L))
                .add(new AbstractMap.SimpleImmutableEntry<>("b", 4L))
                .add(new AbstractMap.SimpleImmutableEntry<>("c", 5L))
                .build(), ImmutableList.copyOf(TEST::iterator));

        Assert.assertEquals(3, TEST.size());
    }

    @Test
    public void GIVEN_a_set_of_items_in_an_ordered_set_WHEN_removing_an_item_by_key_THEN_ensure_the_set_remains_sorted() {
        Assert.assertNull(TEST.put("a", 2L));
        Assert.assertNull(TEST.put("b", 4L));
        Assert.assertNull(TEST.put("c", 1L));
        Assert.assertEquals(Long.valueOf(2L), TEST.remove("a"));

        Assert.assertEquals(ImmutableList.builder()
                .add(new AbstractMap.SimpleImmutableEntry<>("c", 1L))
                .add(new AbstractMap.SimpleImmutableEntry<>("b", 4L))
                .build(), ImmutableList.copyOf(TEST::iterator));

        Assert.assertEquals(2, TEST.size());
    }

    @Test
    public void GIVEN_a_set_of_items_with_one_item_in_it_WHEN_removing_it_by_key_THEN_ensure_the_set_becomes_empty() {
        Assert.assertNull(TEST.put("a", 1L));
        Assert.assertEquals(Long.valueOf(1L), TEST.remove("a"));
        Assert.assertEquals(ImmutableList.of(), ImmutableList.copyOf(TEST::iterator));
        Assert.assertEquals(0, TEST.size());
    }

    @Test
    public void GIVEN_a_set_of_items_WHEN_removing_an_item_by_key_that_does_not_exist_THEN_do_nothing() {
        Assert.assertNull(TEST.put("a", 2L));
        Assert.assertNull(TEST.put("b", 4L));
        Assert.assertNull(TEST.put("c", 1L));
        Assert.assertNull(TEST.remove("d"));

        Assert.assertEquals(ImmutableList.builder()
                .add(new AbstractMap.SimpleImmutableEntry<>("c", 1L))
                .add(new AbstractMap.SimpleImmutableEntry<>("a", 2L))
                .add(new AbstractMap.SimpleImmutableEntry<>("b", 4L))
                .build(), ImmutableList.copyOf(TEST::iterator));

        Assert.assertEquals(3, TEST.size());
    }

    @Test
    public void GIVEN_a_set_of_items_with_the_same_value_throughout_WHEN_removing_an_item_by_key_THEN_ensure_the_single_item_is_removed() {
        Assert.assertNull(TEST.put("a", 1L));
        Assert.assertNull(TEST.put("b", 1L));
        Assert.assertNull(TEST.put("c", 1L));
        Assert.assertEquals(Long.valueOf(1L), TEST.remove("a"));

        Assert.assertEquals(ImmutableList.builder()
                .add(new AbstractMap.SimpleImmutableEntry<>("b", 1L))
                .add(new AbstractMap.SimpleImmutableEntry<>("c", 1L))
                .build(), ImmutableList.copyOf(TEST::iterator));

        Assert.assertEquals(2, TEST.size());
    }

    @Test
    public void GIVEN_a_set_of_items_WHEN_determining_the_head_entry_or_also_known_as_lowest_value_THEN_get_the_lowest_valued_item() {
        Assert.assertNull(TEST.put("a", 2L));
        Assert.assertNull(TEST.put("b", 4L));
        Assert.assertNull(TEST.put("c", 1L));
        Assert.assertEquals(new AbstractMap.SimpleImmutableEntry<>("c", 1L), TEST.headEntry());
        Assert.assertEquals("c", TEST.headKey());
        Assert.assertEquals(Long.valueOf(1L), TEST.headValue());
        Assert.assertEquals(3, TEST.size());
    }

    @Test
    public void GIVEN_a_set_of_items_WHEN_clearing_it_THEN_ensure_the_set_becomes_empty() {
        Assert.assertNull(TEST.put("a", 2L));
        Assert.assertNull(TEST.put("b", 4L));
        Assert.assertNull(TEST.put("c", 1L));
        TEST.clear();
        Assert.assertEquals(ImmutableList.of(), ImmutableList.copyOf(TEST::iterator));
        Assert.assertEquals(0, TEST.size());
    }

    @Test
    public void GIVEN_a_set_of_items_WHEN_retrieving_the_item_by_key_THEN_get_the_item() {
        Assert.assertNull(TEST.put("a", 1L));
        Assert.assertEquals(Long.valueOf(1L), TEST.get("a"));
        Assert.assertEquals(1, TEST.size());
    }

    @Test
    public void GIVEN_a_set_of_items_WHEN_determining_whether_the_item_that_exists_in_the_set_is_indeed_in_the_set_THEN_show_the_item_exists() {
        Assert.assertNull(TEST.put("a", 1L));
        Assert.assertTrue(TEST.containsKey("a"));
        Assert.assertEquals(1, TEST.size());
    }
}
