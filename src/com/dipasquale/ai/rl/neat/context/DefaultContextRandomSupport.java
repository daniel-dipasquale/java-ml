package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.output.OutputClassifier;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class DefaultContextRandomSupport implements Context.RandomSupport {
    private ObjectProfile<RandomSupport> integerRandomSupportProfile;
    private ObjectProfile<RandomSupport> floatRandomSupportProfile;

    @Override
    public int generateIndex(final int offset, final int count) {
        return integerRandomSupportProfile.getObject().next(offset, count);
    }

    @Override
    public boolean isLessThan(final float rate) {
        return floatRandomSupportProfile.getObject().isLessThan(rate);
    }

    @Override
    public <T> T generateItem(final OutputClassifier<T> outputClassifier) {
        return outputClassifier.resolve(floatRandomSupportProfile.getObject().next());
    }

    public void save(final SerializableStateGroup state) {
        state.put("random.integerRandomSupportProfile", integerRandomSupportProfile);
        state.put("random.floatRandomSupportProfile", floatRandomSupportProfile);
    }

    public void load(final SerializableStateGroup state, final IterableEventLoop eventLoop) {
        integerRandomSupportProfile = ObjectProfile.switchProfile(state.get("random.integerRandomSupportProfile"), eventLoop != null);
        floatRandomSupportProfile = ObjectProfile.switchProfile(state.get("random.floatRandomSupportProfile"), eventLoop != null);
    }
}
