package com.dipasquale.synchronization.event.loop;

import java.util.List;

@FunctionalInterface
public interface EventLoopSelector {
    int nextIndex(List<EventLoop> eventLoops);
}
