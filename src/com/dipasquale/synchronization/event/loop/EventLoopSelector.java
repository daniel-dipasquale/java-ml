package com.dipasquale.synchronization.event.loop;

public interface EventLoopSelector {
    int next();

    int size();
}
