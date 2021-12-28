package com.dipasquale.simulation.openai.gym.client;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.net.http.HttpClient;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter(AccessLevel.PACKAGE)
public enum HttpVersion {
    HTTP_1_1(HttpClient.Version.HTTP_1_1, "http"),
    HTTP_2(HttpClient.Version.HTTP_2, "https");

    private final HttpClient.Version httpClientVersion;
    private final String defaultProtocol;
}
