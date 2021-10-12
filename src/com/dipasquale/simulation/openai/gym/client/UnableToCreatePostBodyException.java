package com.dipasquale.simulation.openai.gym.client;

import com.dipasquale.io.serialization.json.JsonObject;
import lombok.Getter;

import java.io.IOException;
import java.io.Serial;

@Getter
public final class UnableToCreatePostBodyException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 5474345944459919040L;
    private static final String MESSAGE = "unable to create the post body";
    private final JsonObject body;

    UnableToCreatePostBodyException(final JsonObject body, final IOException cause) {
        super(MESSAGE, cause);
        this.body = body;
    }
}
