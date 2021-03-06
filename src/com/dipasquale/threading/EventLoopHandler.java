package com.dipasquale.threading;

public interface EventLoopHandler {
    boolean shouldReQueue();

    long getDelayTime();

    void handle();
}
