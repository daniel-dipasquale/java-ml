package com.dipasquale.threading.event.loop;

public interface IntervalEventLoopHandler extends EventLoopHandler {
    boolean shouldRequeue();
}
