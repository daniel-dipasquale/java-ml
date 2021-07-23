package com.dipasquale.common.concurrent;

import com.dipasquale.common.DoubleFactory;

public interface DoubleBiFactory extends DoubleFactory {
    DoubleBiFactory selectContended(boolean contended);
}
