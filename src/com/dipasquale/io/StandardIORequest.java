package com.dipasquale.io;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class StandardIORequest {
    private static final String EMPTY = "";
    private final RequestMethod method;
    private final String resource;
    private final Map<String, String> headers;
    private final String body;

    @Builder
    private static StandardIORequest create(final RequestMethod method, final String resource, final Map<String, String> headers, final String body) {
        return new StandardIORequest(method, resource, Map.copyOf(headers), Objects.requireNonNullElse(body, EMPTY));
    }
}
