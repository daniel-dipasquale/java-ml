package com.dipasquale.io;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public final class StandardIOResponse {
    private final int statusCode;
    private final Map<String, String> headers;
    private final String body;
}
