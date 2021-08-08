package com.dipasquale.threading.wait.handle;

@FunctionalInterface
public interface WaitHandleStrategy {
    boolean shouldAwait(int attempt);
}
