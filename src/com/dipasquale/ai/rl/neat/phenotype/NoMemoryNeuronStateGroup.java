package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.common.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NoMemoryNeuronStateGroup implements NeuronStateGroup {
    private final Map<Id, State> states = new HashMap<>();

    @Override
    public float getValue(final Id id) {
        State state = states.get(id);

        if (state == null) {
            return 0f;
        }

        return state.value;
    }

    @Override
    public float getValue(final Id id, final Id inputId) {
        return getValue(id);
    }

    @Override
    public void setValue(final Id id, final float value) {
        states.put(id, new State(value));
    }

    @Override
    public void addValue(final Id id, final float value, final Id inputId) {
        states.computeIfAbsent(id, k -> new State(0f)).value += value;
    }

    @Override
    public void clear() {
        states.clear();
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class State {
        private float value;
    }
}
