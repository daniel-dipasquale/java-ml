package com.dipasquale.threading;

public interface EventLoopQueueableHandler {
    boolean shouldReQueue();

    long getDelayTime();

    void handle(String name);
}
