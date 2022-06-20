package com.dipasquale.synchronization.lock;

import com.dipasquale.common.factory.ObjectCloner;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class UnboundedRcuController extends AbstractRcuController {
    @Serial
    private static final long serialVersionUID = -611129830885413976L;
    private transient ThreadLocal<ReadToken> readToken = ThreadLocal.withInitial(ReadToken::new);

    @Override
    protected Object getReadToken() {
        return readToken.get().value;
    }

    @Override
    protected void clearReadToken() {
        readToken.remove();
    }

    @Override
    protected void setReadToken(final Object token) {
        readToken.get().value = token;
    }

    @Serial
    private void readObject(final ObjectInputStream objectInputStream)
            throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        readToken = ThreadLocal.withInitial(ReadToken::new);
    }

    @Override
    public <T> RcuMonitoredReference<T> createMonitoredReference(final T reference, final ObjectCloner<T> referenceCloner) {
        return new UnboundedRcuMonitoredReference<>(this, reference, referenceCloner);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class ReadToken {
        private Object value = null;
    }
}
