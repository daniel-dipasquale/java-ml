package com.dipasquale.synchronization.event.loop;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@ToString
public final class EventLoopId {
    private final int index;
    private final String name;
}
