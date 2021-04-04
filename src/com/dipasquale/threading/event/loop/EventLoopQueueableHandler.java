package com.dipasquale.threading.event.loop;

public interface EventLoopQueueableHandler {
    boolean shouldQueue();

    void handle(String name);
}
