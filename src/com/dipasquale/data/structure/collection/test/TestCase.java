package com.dipasquale.data.structure.collection.test;

import java.util.List;

@FunctionalInterface
interface TestCase<T> {
    void run(List<T> items);
}
