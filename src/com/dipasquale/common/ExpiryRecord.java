package com.dipasquale.common;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.measure.quantity.Duration;
import javax.measure.unit.Unit;

@Generated
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString(onlyExplicitlyIncluded = true)
public final class ExpiryRecord {
    @ToString.Include
    private final long currentDateTime;
    @ToString.Include
    private final long expiryDateTime;
    private final Unit<Duration> unit;

    public boolean isExpired(final long dateTime) {
        return currentDateTime >= dateTime;
    }
}
