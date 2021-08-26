package com.dipasquale.synchronization.wait.handle;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.concurrent.TimeUnit;

@Generated
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@EqualsAndHashCode
@ToString
final class TimeUnitPair {
    private final long time;
    private final TimeUnit unit;
}
