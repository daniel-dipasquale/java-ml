package com.dipasquale.simulation.cart.pole;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter(AccessLevel.PACKAGE)
public final class Cart {
    @Builder.Default
    private final double mass = 1D;
}
