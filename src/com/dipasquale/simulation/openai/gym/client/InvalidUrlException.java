package com.dipasquale.simulation.openai.gym.client;

import lombok.Getter;

import java.io.Serial;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

@Getter
public final class InvalidUrlException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -858630038065874240L;
    private static final String MESSAGE_FORMAT = "invalid url: '%s'";
    private final String url;

    InvalidUrlException(final String url, final URISyntaxException cause) {
        super(String.format(MESSAGE_FORMAT, url), cause);
        this.url = url;
    }

    InvalidUrlException(final String url, final MalformedURLException cause) {
        super(String.format(MESSAGE_FORMAT, url), cause);
        this.url = url;
    }
}
