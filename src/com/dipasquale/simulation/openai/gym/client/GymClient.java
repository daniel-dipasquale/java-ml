package com.dipasquale.simulation.openai.gym.client;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;

@RequiredArgsConstructor
public final class GymClient { // TODO: finish this up
    private static final String URL = "http://localhost:5000";
    private final String url;
    private static final String ENVIRONMENTS_API = "/v1/envs/";

    public GymClient() {
        this(URL);
    }

    public Set<String> getEnvironmentInstanceIds() {
        HttpClient httpClient = HttpClient.newHttpClient();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(new URI(String.format("%s%s", url, ENVIRONMENTS_API)))
                    .header("content-type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new RuntimeException("the request was interrupted", e);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("unknown exception occurred while making a request", e);
        }

        return null;
    }
}
