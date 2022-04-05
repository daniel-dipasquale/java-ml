package com.dipasquale.io;

import java.io.IOException;
import java.io.Serial;
import java.util.Map;

public final class UnsupportedStatusCodeException extends IOException {
    @Serial
    private static final long serialVersionUID = -3918161883495354964L;
    private final int statusCode;
    private final Map<String, String> headers;
    private final String body;

    UnsupportedStatusCodeException(final String message, final int statusCode, final Map<String, String> headers, final String body) {
        super(message);
        this.statusCode = statusCode;
        this.headers = Map.copyOf(headers);
        this.body = body;
    }
}
