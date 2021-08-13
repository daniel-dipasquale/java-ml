/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.collection;

import com.google.common.collect.ImmutableList;
import lombok.Generated;

import java.util.Collection;
import java.util.List;
import java.util.function.IntFunction;

@Generated // TODO: should the testing tool be tested? I'm feeling like it should be
final class RemoveAllAsserter<T> extends AsserterBase<T> {
    private static final int ITEM_COUNT = 5;
    private final Collection<T> collection;
    private final IntFunction<T> itemFactory;
    private final AssertEquals equalsAsserter;

    RemoveAllAsserter(final TestCaseCollectionRunner<T> testCaseRunner, final Collection<T> collection, final IntFunction<T> itemFactory, final AssertEquals equalsAsserter) {
        super(testCaseRunner, ITEM_COUNT);
        this.collection = collection;
        this.itemFactory = itemFactory;
        this.equalsAsserter = equalsAsserter;
    }

    private void assertNoneExist(final List<T> items) {
        List<T> itemsToRemove = ImmutableList.<T>builder()
                .add(itemFactory.apply(5))
                .add(itemFactory.apply(6))
                .build();

        equalsAsserter.assertEquals(false, collection.removeAll(itemsToRemove));
        equalsAsserter.assertEquals(items, ImmutableList.copyOf(collection));
    }

    private void assertSomeExist(final List<T> items) {
        List<T> itemsToRemove = ImmutableList.<T>builder()
                .add(items.get(0))
                .add(itemFactory.apply(5))
                .build();

        List<T> expected = ImmutableList.<T>builder()
                .add(items.get(1))
                .add(items.get(2))
                .add(items.get(3))
                .add(items.get(4))
                .build();

        equalsAsserter.assertEquals(true, collection.removeAll(itemsToRemove));
        equalsAsserter.assertEquals(expected, ImmutableList.copyOf(collection));
    }

    private void assertSubsetExist(final List<T> items) {
        List<T> itemsToRemove = ImmutableList.<T>builder()
                .add(items.get(1))
                .add(items.get(2))
                .build();

        List<T> expected = ImmutableList.<T>builder()
                .add(items.get(0))
                .add(items.get(3))
                .add(items.get(4))
                .build();

        equalsAsserter.assertEquals(true, collection.removeAll(itemsToRemove));
        equalsAsserter.assertEquals(expected, ImmutableList.copyOf(collection));
    }

    private void assertAllExist(final List<T> items) {
        List<T> expected = ImmutableList.of();

        equalsAsserter.assertEquals(true, collection.removeAll(items));
        equalsAsserter.assertEquals(expected, ImmutableList.copyOf(collection));
    }

    @Override
    protected List<TestCaseCollection<T>> createTestCases() {
        return ImmutableList.<TestCaseCollection<T>>builder()
                .add(TestCaseCollection.create(this::assertNoneExist, true))
                .add(TestCaseCollection.create(this::assertSomeExist, true))
                .add(TestCaseCollection.create(this::assertSubsetExist, true))
                .add(TestCaseCollection.create(this::assertAllExist, true))
                .build();
    }
}
