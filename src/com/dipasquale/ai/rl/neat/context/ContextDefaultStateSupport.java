package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.data.structure.map.SerializableInteroperableStateMap;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@RequiredArgsConstructor
public final class ContextDefaultStateSupport implements Context.StateSupport {
    private final ContextDefault context;

    @Override
    public void save(final ObjectOutputStream outputStream)
            throws IOException {
        SerializableInteroperableStateMap state = new SerializableInteroperableStateMap();

        context.save(state);
        state.writeTo(outputStream);
    }

    @Override
    public void load(final ObjectInputStream inputStream, final Context.StateOverrideSupport override)
            throws IOException, ClassNotFoundException {
        SerializableInteroperableStateMap state = new SerializableInteroperableStateMap();

        state.readFrom(inputStream);
        context.load(state, override);
    }
}
