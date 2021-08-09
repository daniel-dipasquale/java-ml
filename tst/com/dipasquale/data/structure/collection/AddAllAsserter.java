package com.dipasquale.data.structure.collection;

import com.google.common.collect.ImmutableList;
import lombok.Generated;

import java.util.Collection;
import java.util.List;

@Generated // TODO: should the testing tool be tested? I'm feeling like it should be
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
        equalsAsserter.assertEquals(items, ImmutableList.copyOf(collection));
    }

    private void assertAllItems(final List<T> items) {
        equalsAsserter.assertEquals(true, collection.addAll(items));
        equalsAsserter.assertEquals(items, ImmutableList.copyOf(collection));
    }

    @Override
    protected List<TestCaseCollection<T>> createTestCases() {
        return ImmutableList.<TestCaseCollection<T>>builder()
                .add(TestCaseCollection.create(this::assertEmptyItems, false))
                .add(TestCaseCollection.create(this::assertAllItems, false))
                .build();
    }
}
