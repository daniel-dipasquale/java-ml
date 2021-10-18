package com.dipasquale.simulation.openai.gym.client;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ResponseHandler<T> {
    private final String expectedContentType;
    private final Delegate<T> delegate;

    public boolean ableToHandle(final String contentType) {
        return expectedContentType == null || contentType != null && contentType.startsWith(expectedContentType);
    }

    public T handle(final HttpResponse<InputStream> response)
            throws IOException {
        return delegate.handle(response);
    }

    @FunctionalInterface
    interface Delegate<T> {
        T handle(HttpResponse<InputStream> response) throws IOException;
    }
}
