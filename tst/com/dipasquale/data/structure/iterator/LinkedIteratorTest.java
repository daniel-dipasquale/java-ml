package com.dipasquale.data.structure.iterator;

import com.dipasquale.data.structure.collection.ListSupport;
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
    private static List<LinkedElement> createLinkedElements(final int count) {
        if (count == 0) {
            return null;
        }

        List<LinkedElement> linkedElements = IntStream.range(1, count + 1)
                .mapToObj(index -> new LinkedElement(String.format("val-%d", index), null))
                .collect(Collectors.toList());

        for (int i = 1; i < count; i++) {
            linkedElements.get(i - 1).next = linkedElements.get(i);
        }

        return linkedElements;
    }

    private static List<LinkedElement> createCircularLinkedElements(final int count) {
        List<LinkedElement> linkedElements = createLinkedElements(count);

        if (linkedElements != null) {
            linkedElements.get(count - 1).next = linkedElements.get(0);
        }

        return linkedElements;
    }

    @Test
    public void GIVEN_an_endless_linked_list_in_the_form_of_a_circular_linked_list_WHEN_iterating_through_it_with_a_limit_THEN_iterate_through_all_elements_until_the_limit_is_hit_before_terminating_the_iteration() {
        LinkedElement linkedElement = createCircularLinkedElements(3).get(0);

        Stream<String> result = LinkedIterator.createStream(linkedElement, __linkedElement -> __linkedElement.next)
                .map(__linkedElement -> __linkedElement.value)
                .limit(2);

        Assertions.assertEquals(List.of("val-1", "val-2"), ListSupport.copyOf(result.iterator()));
    }

    @Test
    public void GIVEN_a_linked_list_with_a_natural_end_WHEN_iterating_through_it_THEN_iterate_through_all_elements() {
        LinkedElement linkedElement = createLinkedElements(3).get(0);

        Stream<String> result = LinkedIterator.createStream(linkedElement, __linkedElement -> __linkedElement.next)
                .map(__linkedElement -> __linkedElement.value);

        Assertions.assertEquals(List.of("val-1", "val-2", "val-3"), ListSupport.copyOf(result.iterator()));
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    private static class LinkedElement {
        private final String value;
        private LinkedElement next;
    }
}
