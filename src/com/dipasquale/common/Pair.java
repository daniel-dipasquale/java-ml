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
public final class Pair<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -939235943898272440L;
    private final T left;
    private final T right;
}
