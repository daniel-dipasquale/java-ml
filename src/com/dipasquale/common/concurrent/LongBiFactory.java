package com.dipasquale.common.concurrent;

import com.dipasquale.common.LongFactory;

public interface LongBiFactory extends LongFactory {
    LongFactory selectContended(boolean contended);
}
