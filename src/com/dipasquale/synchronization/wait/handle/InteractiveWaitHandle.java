package com.dipasquale.synchronization.wait.handle;

public interface InteractiveWaitHandle extends WaitHandle {
    void start();

    void complete();
}
