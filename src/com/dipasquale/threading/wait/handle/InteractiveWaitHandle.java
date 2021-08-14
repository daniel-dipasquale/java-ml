package com.dipasquale.threading.wait.handle;

public interface InteractiveWaitHandle extends WaitHandle {
    void start();

    void complete();
}
