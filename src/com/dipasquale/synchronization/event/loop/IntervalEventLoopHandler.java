package com.dipasquale.synchronization.event.loop;

public interface IntervalEventLoopHandler extends EventLoopHandler {
    boolean shouldRequeue();
}
