package com.dipasquale.io;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum RequestMethod {
    GET("GET"),
    POST("POST");

    private final String value;
}
