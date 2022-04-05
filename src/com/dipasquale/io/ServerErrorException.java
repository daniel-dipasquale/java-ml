package com.dipasquale.io;

import lombok.Getter;

import java.io.IOException;
import java.io.Serial;
import java.util.Map;

@Getter
public final class ServerErrorException extends IOException {
    @Serial
    private static final long serialVersionUID = -156776953155838704L;
    private final int statusCode;
    private final Map<String, String> headers;
    private final String body;

    ServerErrorException(final String message, final int statusCode, final Map<String, String> headers, final String body) {
        super(message);
        this.statusCode = statusCode;
        this.headers = Map.copyOf(headers);
        this.body = body;
    }
}
