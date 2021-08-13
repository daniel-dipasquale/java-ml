/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.collection;

import java.util.List;

@FunctionalInterface
interface TestCase<T> {
    void run(List<T> items);
}
