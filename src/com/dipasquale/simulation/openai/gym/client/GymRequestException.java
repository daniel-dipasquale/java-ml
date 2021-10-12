package com.dipasquale.simulation.openai.gym.client;

import lombok.Getter;

import java.io.Serial;
import java.util.Map;

@Getter
public final class GymRequestException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 741745110525361228L;
    private final int statusCode;
    private final Map<String, String> headers;
    private final Object body;

    GymRequestException(final String message, final int statusCode, final Map<String, String> headers, final Object body) {
        super(message);
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }

    GymRequestException(final String message, final int statusCode, final Map<String, String> headers, final Object body, final Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }
}
