package com.dipasquale.simulation.openai.gym.client;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class GymClient implements Closeable {
    private static final String DEFAULT_BASE_URL = "http://localhost:5000";
    private static final String DEFAULT_INSTANCE_ID = "__general";
    private Map<String, Map<String, String>> environments;
    private final InternalGymClient client;

    private GymClient(final String baseUrl) {
        this.environments = new HashMap<>();
        this.client = new InternalGymClient(baseUrl);
    }

    public GymClient() {
        this(DEFAULT_BASE_URL);
    }

    public GymClient(final String protocol, final String domain, final int port) {
        this(getBaseUrl(protocol, domain, port));
    }

    public GymClient(final String domain, final int port) {
        this("http", domain, port);
    }

    private static String getBaseUrl(final String protocol, final String domain, final int port) {
        return String.format("%s://%s:%d", protocol, domain, port);
    }

    private Map<String, String> getOrCreateInternalInstances(final String environmentId) {
        return environments.computeIfAbsent(environmentId, __ -> new HashMap<>());
    }

    private static String getValidInstanceId(final String id) {
        if (id != null) {
            return id;
        }

        return DEFAULT_INSTANCE_ID;
    }

    private String getOrCreateInternalInstanceId(final String environmentId, final String instanceId) {
        Map<String, String> internalInstances = getOrCreateInternalInstances(environmentId);
        String fixedInstanceId = getValidInstanceId(instanceId);
        String internalInstanceId = internalInstances.get(fixedInstanceId);

        if (internalInstanceId == null) {
            internalInstanceId = client.createEnvironment(environmentId);
            internalInstances.put(fixedInstanceId, internalInstanceId);
        }

        return internalInstanceId;
    }

    public double[] restart(final String environmentId, final String instanceId) {
        String internalInstanceId = getOrCreateInternalInstanceId(environmentId, instanceId);

        return client.reset(internalInstanceId);
    }

    public double[] restart(final String environmentId) {
        return restart(environmentId, null);
    }

    public Map<String, Object> getActionSpace(final String environmentId, final String instanceId) {
        String internalInstanceId = getOrCreateInternalInstanceId(environmentId, instanceId);

        return client.getActionSpace(internalInstanceId);
    }

    public Map<String, Object> getActionSpace(final String environmentId) {
        return getActionSpace(environmentId, null);
    }

    public StepResult step(final String environmentId, final String instanceId, final double action, final boolean render) {
        String internalInstanceId = getOrCreateInternalInstanceId(environmentId, instanceId);

        return client.step(internalInstanceId, action, render);
    }

    public StepResult step(final String environmentId, final String instanceId, final double action) {
        return step(environmentId, instanceId, action, false);
    }

    public StepResult step(final String environmentId, final double action) {
        return step(environmentId, null, action);
    }

    public StepResult step(final String environmentId, final double action, final boolean render) {
        return step(environmentId, null, action, render);
    }

    public void startMonitor(final String environmentId, final String instanceId, final boolean force, final boolean resume) {
        String internalInstanceId = getOrCreateInternalInstanceId(environmentId, instanceId);

        client.startMonitor(internalInstanceId, force, resume);
    }

    public void startMonitor(final String environmentId, final String instanceId) {
        startMonitor(environmentId, instanceId, false, false);
    }

    public void startMonitor(final String environmentId) {
        startMonitor(environmentId, null);
    }

    public void closeMonitor(final String environmentId, final String instanceId) {
        String internalInstanceId = getOrCreateInternalInstanceId(environmentId, instanceId);

        client.closeMonitor(internalInstanceId);
    }

    public void closeMonitor(final String environmentId) {
        closeMonitor(environmentId, null);
    }

    public void upload(final String trainingDirectory, final String apiKey, final String algorithmId) {
        client.upload(trainingDirectory, apiKey, algorithmId);
    }

    public void shutdown() {
        client.shutdown();
    }

    @Override
    public void close() {
        client.close();
    }
}
