package com.dipasquale.simulation.game2048;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public final class ValuedTile {
    private final int tileId;
    private final int exponentialValue;
}
