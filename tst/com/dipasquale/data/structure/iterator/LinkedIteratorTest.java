package com.dipasquale.data.structure.iterator;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class LinkedIteratorTest {
    private static List<LinkedItem> createLinkedItems(final int count) {
        if (count == 0) {
            return null;
        }

        List<LinkedItem> linkedItems = IntStream.range(1, count + 1)
                .mapToObj(i -> new LinkedItem(String.format("val-%d", i), null))
                .collect(Collectors.toList());

        for (int i = 1; i < count; i++) {
            linkedItems.get(i - 1).next = linkedItems.get(i);
        }

        return linkedItems;
    }

    private static List<LinkedItem> createCircularLinkedItems(final int count) {
        List<LinkedItem> linkedItems = createLinkedItems(count);

        if (linkedItems != null) {
            linkedItems.get(count - 1).next = linkedItems.get(0);
        }

        return linkedItems;
    }

    @Test
    public void GIVEN_an_endless_linked_list_in_the_form_of_a_circular_linked_list_WHEN_iterating_through_it_with_a_limit_THEN_iterate_through_all_items_until_the_limit_is_hit_before_terminating_the_iteration() {
        LinkedItem linkedItem = createCircularLinkedItems(3).get(0);

        Stream<String> result = LinkedIterator.createStream(linkedItem, li -> li.next)
                .map(li -> li.value)
                .limit(2);

        Assertions.assertEquals(ImmutableList.<String>builder()
                .add("val-1")
                .add("val-2")
                .build(), ImmutableList.copyOf(result::iterator));
    }

    @Test
    public void GIVEN_a_linked_list_with_a_natural_end_WHEN_iterating_through_it_THEN_iterate_through_all_items() {
        LinkedItem linkedItem = createLinkedItems(3).get(0);

        Stream<String> result = LinkedIterator.createStream(linkedItem, li -> li.next)
                .map(li -> li.value);

        Assertions.assertEquals(ImmutableList.<String>builder()
                .add("val-1")
                .add("val-2")
                .add("val-3")
                .build(), ImmutableList.copyOf(result::iterator));
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    private static class LinkedItem {
        private final String value;
        private LinkedItem next;
    }
}
