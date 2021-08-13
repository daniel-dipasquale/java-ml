package com.dipasquale.threading.event.loop;

public interface EventLoopSelector {
    int next();

    int size();
}
