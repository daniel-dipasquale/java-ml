package com.dipasquale.simulation.cart.pole;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter(AccessLevel.PACKAGE)
public final class Pole {
    @Builder.Default
    private final double mass = 0.1D;
    @Builder.Default
    private final double length = 1D;
}
