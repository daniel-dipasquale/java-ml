package com.dipasquale.ai.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class JointItems<T> {
    private final T item1;
    private final T item2;
}
