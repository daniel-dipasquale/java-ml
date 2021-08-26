package com.dipasquale.synchronization.wait.handle;

@FunctionalInterface
public interface WaitHandleStrategy {
    boolean shouldAwait(int attempt);
}
