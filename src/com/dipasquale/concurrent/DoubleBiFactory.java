package com.dipasquale.concurrent;

import com.dipasquale.common.DoubleFactory;

public interface DoubleBiFactory extends DoubleFactory {
    DoubleBiFactory selectContended(boolean contended);
}
