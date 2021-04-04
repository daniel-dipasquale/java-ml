package com.dipasquale.threading.event.loop;

public interface EventLoopQueueableHandler {
    boolean shouldReQueue();

    long getDelayTime();

    void handle(String name);
}
