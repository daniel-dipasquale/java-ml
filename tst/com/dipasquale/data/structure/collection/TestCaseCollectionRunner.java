package com.dipasquale.data.structure.collection;

import lombok.AccessLevel;
import lombok.Generated;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Generated // TODO: should the testing tool be tested? I'm feeling like it should be
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class TestCaseCollectionRunner<T> {
    private final Collection<T> collection;
    private final IntFunction<T> itemFactory;
    private final AssertEquals equalsAsserter;

    public void run(final int count, final List<TestCaseCollection<T>> testCases) {
        for (TestCaseCollection<T> testCase : testCases) {
            collection.clear();

            List<T> items = IntStream.range(0, count)
                    .mapToObj(itemFactory)
                    .collect(Collectors.toList());

            if (testCase.shouldFillCollection()) {
                for (T item : items) {
                    equalsAsserter.assertEquals(true, collection.add(item));
                }
            }

            testCase.run(items);
        }
    }
}
