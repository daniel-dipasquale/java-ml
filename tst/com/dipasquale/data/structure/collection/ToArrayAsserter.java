package com.dipasquale.data.structure.collection;

import lombok.Generated;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;

@Generated // TODO: should the testing tool be tested? I'm feeling like it should be
final class ToArrayAsserter<T> extends AsserterBase<T> {
    private static final int ITEM_COUNT = 5;
    private final Collection<T> collection;
    private final Class<?> itemType;
    private final AssertEquals equalsAsserter;

    ToArrayAsserter(final TestCaseCollectionRunner<T> testCaseRunner, final Collection<T> collection, final Class<? super T> itemType, final AssertEquals equalsAsserter) {
        super(testCaseRunner, ITEM_COUNT);
        this.collection = collection;
        this.itemType = itemType;
        this.equalsAsserter = equalsAsserter;
    }

    private void assertEmptyItems(final List<T> items) {
        items.clear();
        equalsAsserter.assertEquals(Lists.create(items.toArray()), Lists.create(collection.toArray()));
    }

    private void assertAllItems(final List<T> items) {
        equalsAsserter.assertEquals(Lists.create(items.toArray()), Lists.create(collection.toArray()));
    }

    private T[] createArray(final int count) {
        return (T[]) Array.newInstance(itemType, ITEM_COUNT);
    }

    private void assertEmptyItemsUsingArrayFactory(final List<T> items) {
        items.clear();
        equalsAsserter.assertEquals(Lists.create(items.toArray(this::createArray)), Lists.create(collection.toArray(this::createArray)));
    }

    private void assertAllItemsUsingArrayFactory(final List<T> items) {
        equalsAsserter.assertEquals(Lists.create(items.toArray(this::createArray)), Lists.create(collection.toArray(this::createArray)));
    }

    private void assertEmptyItemsUsingEmptyArray(final List<T> items) {
        items.clear();
        equalsAsserter.assertEquals(Lists.create(items.toArray(createArray(0))), Lists.create(collection.toArray(createArray(0))));
    }

    private void assertAllItemsUsingEmptyArray(final List<T> items) {
        equalsAsserter.assertEquals(Lists.create(items.toArray(createArray(0))), Lists.create(collection.toArray(createArray(0))));
    }

    private void assertEmptyItemsUsingArray(final List<T> items) {
        items.clear();
        equalsAsserter.assertEquals(Lists.create(items.toArray(createArray(ITEM_COUNT))), Lists.create(collection.toArray(createArray(ITEM_COUNT))));
    }

    private void assertAllItemsUsingArray(final List<T> items) {
        equalsAsserter.assertEquals(Lists.create(items.toArray(createArray(ITEM_COUNT))), Lists.create(collection.toArray(createArray(ITEM_COUNT))));
    }

    @Override
    protected List<TestCaseCollection<T>> createTestCases() {
        return List.of(
                TestCaseCollection.create(this::assertEmptyItems, false),
                TestCaseCollection.create(this::assertAllItems, true),
                TestCaseCollection.create(this::assertEmptyItemsUsingArrayFactory, false),
                TestCaseCollection.create(this::assertAllItemsUsingArrayFactory, true),
                TestCaseCollection.create(this::assertEmptyItemsUsingEmptyArray, false),
                TestCaseCollection.create(this::assertAllItemsUsingEmptyArray, true),
                TestCaseCollection.create(this::assertEmptyItemsUsingArray, false),
                TestCaseCollection.create(this::assertAllItemsUsingArray, true)
        );
    }
}
