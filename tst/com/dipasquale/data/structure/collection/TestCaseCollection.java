package com.dipasquale.data.structure.collection;

import lombok.Generated;

import java.util.List;

@Generated
interface TestCaseCollection<T> { // TODO: should the testing tool be tested? I'm feeling like it should be
    boolean shouldFillCollection();

    void run(List<T> items);

    static <T> TestCaseCollection<T> create(final TestCase<T> testCase, final boolean shouldFillCollection) {
        return new TestCaseCollection<>() {
            @Override
            public boolean shouldFillCollection() {
                return shouldFillCollection;
            }

            @Override
            public void run(final List<T> items) {
                testCase.run(items);
            }
        };
    }
}