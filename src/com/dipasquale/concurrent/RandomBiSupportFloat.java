package com.dipasquale.concurrent;

import com.dipasquale.common.RandomSupportFloat;

public interface RandomBiSupportFloat extends RandomSupportFloat {
    RandomBiSupportFloat selectContended(boolean contended);
}
