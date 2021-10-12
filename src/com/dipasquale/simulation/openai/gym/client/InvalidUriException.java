package com.dipasquale.simulation.openai.gym.client;

import lombok.Getter;

import java.io.Serial;
import java.net.URISyntaxException;

@Getter
public final class InvalidUriException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -858630038065874240L;
    private static final String MESSAGE_FORMAT = "invalid uri: '%s'";
    private final String uri;

    InvalidUriException(final String uri, final URISyntaxException cause) {
        super(String.format(MESSAGE_FORMAT, uri), cause);
        this.uri = uri;
    }
}
