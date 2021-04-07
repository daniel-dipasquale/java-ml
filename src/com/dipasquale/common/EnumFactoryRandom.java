package com.dipasquale.common;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class EnumFactoryRandom<T extends Enum<T>> implements EnumFactory<T> {
    @Serial
    private static final long serialVersionUID = -4891938161087597067L;
    private final RandomSupportFloat randomSupport;
    private final List<T> values;

    @Override
    public T create() {
        int index = randomSupport.next(0, values.size());

        return values.get(index);
    }
}
