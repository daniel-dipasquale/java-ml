package com.dipasquale.concurrent;

import com.dipasquale.common.LongFactory;

public interface LongBiFactory extends LongFactory {
    LongFactory selectContended(boolean contended);
}
