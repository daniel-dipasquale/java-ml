package com.dipasquale.ai.rl.neat.common.openai;

import com.dipasquale.common.concurrent.AtomicLazyReference;
import com.dipasquale.simulation.openai.gym.client.GymClient;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GymClientProvider {
    private static final AtomicLazyReference<GymClient> GYM_CLIENT = new AtomicLazyReference<>(GymClientProvider::create);

    private static void startOpenAIGymServer()
            throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("python", "gym_http_server.py");
        String currentDirectory = System.getProperty("user.dir");
        String gymHttpApiDirectory = Path.of(new File(currentDirectory).getParent(), "gym-http-api").toString();

        processBuilder.directory(new File(gymHttpApiDirectory));
        processBuilder.start();
    }

    private static GymClient create() {
        try {
            System.setProperty("jdk.http.auth.proxying.disabledSchemes", "");
            System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
            startOpenAIGymServer();

            return new GymClient();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static GymClient getGymClient() {
        return GYM_CLIENT.getReference();
    }
}
