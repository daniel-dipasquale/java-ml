package com.dipasquale.data.structure.collection;

import lombok.AccessLevel;
import lombok.Generated;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Generated // TODO: should the testing tool be tested? I'm feeling like it should be
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AsserterBase<T> {
    private final TestCaseCollectionRunner<T> testCaseRunner;
    private final int itemCount;

    protected abstract List<TestCaseCollection<T>> createTestCases();

    public final void assertTestCases() {
        List<TestCaseCollection<T>> testCases = createTestCases();

        testCaseRunner.run(itemCount, testCases);
    }
}
