package com.dipasquale.common.random.concurrent;

import com.dipasquale.common.random.RandomSupportFloat;

public interface RandomBiSupportFloat extends RandomSupportFloat {
    RandomBiSupportFloat selectContended(boolean contended);

    static RandomBiSupportFloat create(final RandomSupportFloat randomSupport, final RandomSupportFloat randomSupportContended) {
        return new RandomBiSupportFloatDefault(randomSupport, randomSupportContended);
    }
}
