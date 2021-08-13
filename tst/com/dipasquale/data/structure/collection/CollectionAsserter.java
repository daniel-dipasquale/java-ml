/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.collection;

import lombok.Generated;

import java.util.Collection;
import java.util.function.IntFunction;

@Generated // TODO: should the testing tool be tested? I'm feeling like it should be
public final class CollectionAsserter<T> {
    private final TestCaseCollectionRunner<T> testCaseRunner;
    private final Collection<T> collection;
    private final IntFunction<T> itemFactory;
    private final Class<? super T> itemType;
    private final AssertEquals equalsAsserter;

    public CollectionAsserter(final Collection<T> collection, final IntFunction<T> itemFactory, final Class<? super T> itemType, final AssertEquals equalsAsserter) {
        this.testCaseRunner = new TestCaseCollectionRunner<>(collection, itemFactory, equalsAsserter);
        this.collection = collection;
        this.itemFactory = itemFactory;
        this.itemType = itemType;
        this.equalsAsserter = equalsAsserter;
    }

    public void assertToArray() {
        ToArrayAsserter<T> asserter = new ToArrayAsserter<>(testCaseRunner, collection, itemType, equalsAsserter);

        asserter.assertTestCases();
    }

    public void assertContainsAll() {
        ContainsAllAsserter<T> asserter = new ContainsAllAsserter<>(testCaseRunner, collection, itemFactory, equalsAsserter);

        asserter.assertTestCases();
    }

    public void assertAddAll() {
        AddAllAsserter<T> asserter = new AddAllAsserter<>(testCaseRunner, collection, equalsAsserter);

        asserter.assertTestCases();
    }

    public void assertRetainAll() {
        RetainAllAsserter<T> asserter = new RetainAllAsserter<>(testCaseRunner, collection, itemFactory, equalsAsserter);

        asserter.assertTestCases();
    }

    public void assertRemoveAll() {
        RemoveAllAsserter<T> asserter = new RemoveAllAsserter<>(testCaseRunner, collection, itemFactory, equalsAsserter);

        asserter.assertTestCases();
    }
}
