package com.dipasquale.data.structure.collection;

import lombok.Generated;

import java.util.Collection;
import java.util.List;

@Generated
final class AddAllAsserter<T> extends AsserterBase<T> {
    private static final int ITEM_COUNT = 5;
    private final Collection<T> collection;
    private final AssertEquals equalsAsserter;

    AddAllAsserter(final TestCaseCollectionRunner<T> testCaseRunner, final Collection<T> collection, final AssertEquals equalsAsserter) {
        super(testCaseRunner, ITEM_COUNT);
        this.collection = collection;
        this.equalsAsserter = equalsAsserter;
    }

    private void assertEmptyItems(final List<T> items) {
        items.clear();
        equalsAsserter.assertEquals(false, collection.addAll(items));
        equalsAsserter.assertEquals(items, List.copyOf(collection));
    }

    private void assertAllItems(final List<T> items) {
        equalsAsserter.assertEquals(true, collection.addAll(items));
        equalsAsserter.assertEquals(items, List.copyOf(collection));
    }

    @Override
    protected List<TestCaseCollection<T>> createTestCases() {
        return List.of(
                TestCaseCollection.create(this::assertEmptyItems, false),
                TestCaseCollection.create(this::assertAllItems, false)
        );
    }
}
