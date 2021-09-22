package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.output.OutputClassifier;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class DefaultContextRandomSupport implements Context.RandomSupport {
    private ObjectProfile<RandomSupport> integerRandomSupport;
    private ObjectProfile<RandomSupport> floatRandomSupport;

    @Override
    public int generateIndex(final int offset, final int count) {
        return integerRandomSupport.getObject().next(offset, count);
    }

    @Override
    public boolean isLessThan(final float rate) {
        return floatRandomSupport.getObject().isLessThan(rate);
    }

    @Override
    public <T> T generateItem(final OutputClassifier<T> outputClassifier) {
        return outputClassifier.resolve(floatRandomSupport.getObject().next());
    }

    public void save(final SerializableStateGroup state) {
        state.put("random.integerRandomSupport", integerRandomSupport);
        state.put("random.floatRandomSupport", floatRandomSupport);
    }

    public void load(final SerializableStateGroup state, final IterableEventLoop eventLoop) {
        integerRandomSupport = ObjectProfile.switchProfile(state.get("random.integerRandomSupport"), eventLoop != null);
        floatRandomSupport = ObjectProfile.switchProfile(state.get("random.floatRandomSupport"), eventLoop != null);
    }
}
