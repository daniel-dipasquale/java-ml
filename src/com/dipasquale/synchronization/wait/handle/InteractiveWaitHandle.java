package com.dipasquale.synchronization.wait.handle;

public interface InteractiveWaitHandle extends WaitHandle {
    boolean countUp();

    boolean countDown();
}
