package com.dipasquale.common;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public final class Record<TKey, TValue> implements Serializable {
    @Serial
    private static final long serialVersionUID = -4319420321035293016L;
    private final TKey key;
    private final TValue value;
}
