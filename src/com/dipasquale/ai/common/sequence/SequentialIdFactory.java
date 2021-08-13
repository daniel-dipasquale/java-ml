/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.common.sequence;

import com.dipasquale.common.factory.ObjectFactory;

public interface SequentialIdFactory extends ObjectFactory<SequentialId> {
    void reset();
}
