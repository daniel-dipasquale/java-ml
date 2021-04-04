package com.dipasquale.threading.event.loop;

public interface EventLoopIntervalHandler extends EventLoopHandler {
    boolean shouldRequeue();
}
