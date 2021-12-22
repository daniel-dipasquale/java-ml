package com.dipasquale.data.structure.collection;

import lombok.Generated;

import java.util.Collection;
import java.util.List;
import java.util.function.IntFunction;

@Generated
final class RetainAllAsserter<T> extends AsserterBase<T> {
    private static final int ITEM_COUNT = 5;
    private final Collection<T> collection;
    private final IntFunction<T> itemFactory;
    private final AssertEquals equalsAsserter;

    RetainAllAsserter(final TestCaseCollectionRunner<T> testCaseRunner, final Collection<T> collection, final IntFunction<T> itemFactory, final AssertEquals equalsAsserter) {
        super(testCaseRunner, ITEM_COUNT);
        this.collection = collection;
        this.itemFactory = itemFactory;
        this.equalsAsserter = equalsAsserter;
    }

    private void assertNoneExist(final List<T> items) {
        List<T> itemsToRetain = List.of(itemFactory.apply(5), itemFactory.apply(6));
        List<T> expected = List.of();

        equalsAsserter.assertEquals(true, collection.retainAll(itemsToRetain));
        equalsAsserter.assertEquals(expected, List.copyOf(collection));
    }

    private void assertSomeExist(final List<T> items) {
        List<T> itemsToRetain = List.of(items.get(0), itemFactory.apply(5));
        List<T> expected = List.of(items.get(0));

        equalsAsserter.assertEquals(true, collection.retainAll(itemsToRetain));
        equalsAsserter.assertEquals(expected, List.copyOf(collection));
    }

    private void assertSubsetExist(final List<T> items) {
        List<T> itemsToRetain = List.of(items.get(1), items.get(2));

        equalsAsserter.assertEquals(true, collection.retainAll(itemsToRetain));
        equalsAsserter.assertEquals(itemsToRetain, List.copyOf(collection));
    }

    private void assertAllExist(final List<T> items) {
        equalsAsserter.assertEquals(false, collection.retainAll(items));
        equalsAsserter.assertEquals(items, List.copyOf(collection));
    }

    @Override
    protected List<TestCaseCollection<T>> createTestCases() {
        return List.of(
                TestCaseCollection.create(this::assertNoneExist, true),
                TestCaseCollection.create(this::assertSomeExist, true),
                TestCaseCollection.create(this::assertSubsetExist, true),
                TestCaseCollection.create(this::assertAllExist, true)
        );
    }
}
