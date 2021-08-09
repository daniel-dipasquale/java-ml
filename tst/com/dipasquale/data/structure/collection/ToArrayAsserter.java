package com.dipasquale.data.structure.collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
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
        equalsAsserter.assertEquals(Lists.newArrayList(items.toArray()), Lists.newArrayList(collection.toArray()));
    }

    private void assertAllItems(final List<T> items) {
        equalsAsserter.assertEquals(Lists.newArrayList(items.toArray()), Lists.newArrayList(collection.toArray()));
    }

    private T[] createArray(final int count) {
        return (T[]) Array.newInstance(itemType, ITEM_COUNT);
    }

    private void assertEmptyItemsUsingArrayFactory(final List<T> items) {
        items.clear();
        equalsAsserter.assertEquals(Lists.newArrayList(items.toArray(this::createArray)), Lists.newArrayList(collection.toArray(this::createArray)));
    }

    private void assertAllItemsUsingArrayFactory(final List<T> items) {
        equalsAsserter.assertEquals(Lists.newArrayList(items.toArray(this::createArray)), Lists.newArrayList(collection.toArray(this::createArray)));
    }

    private void assertEmptyItemsUsingEmptyArray(final List<T> items) {
        items.clear();
        equalsAsserter.assertEquals(Lists.newArrayList(items.toArray(createArray(0))), Lists.newArrayList(collection.toArray(createArray(0))));
    }

    private void assertAllItemsUsingEmptyArray(final List<T> items) {
        equalsAsserter.assertEquals(Lists.newArrayList(items.toArray(createArray(0))), Lists.newArrayList(collection.toArray(createArray(0))));
    }

    private void assertEmptyItemsUsingArray(final List<T> items) {
        items.clear();
        equalsAsserter.assertEquals(Lists.newArrayList(items.toArray(createArray(ITEM_COUNT))), Lists.newArrayList(collection.toArray(createArray(ITEM_COUNT))));
    }

    private void assertAllItemsUsingArray(final List<T> items) {
        equalsAsserter.assertEquals(Lists.newArrayList(items.toArray(createArray(ITEM_COUNT))), Lists.newArrayList(collection.toArray(createArray(ITEM_COUNT))));
    }

    @Override
    protected List<TestCaseCollection<T>> createTestCases() {
        return ImmutableList.<TestCaseCollection<T>>builder()
                .add(TestCaseCollection.create(this::assertEmptyItems, false))
                .add(TestCaseCollection.create(this::assertAllItems, true))
                .add(TestCaseCollection.create(this::assertEmptyItemsUsingArrayFactory, false))
                .add(TestCaseCollection.create(this::assertAllItemsUsingArrayFactory, true))
                .add(TestCaseCollection.create(this::assertEmptyItemsUsingEmptyArray, false))
                .add(TestCaseCollection.create(this::assertAllItemsUsingEmptyArray, true))
                .add(TestCaseCollection.create(this::assertEmptyItemsUsingArray, false))
                .add(TestCaseCollection.create(this::assertAllItemsUsingArray, true))
                .build();
    }
}
