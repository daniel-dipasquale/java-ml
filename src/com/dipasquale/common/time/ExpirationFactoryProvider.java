package com.dipasquale.common.time;

public interface ExpirationFactoryProvider {
    ExpirationFactory get(int index);

    int size();
}
