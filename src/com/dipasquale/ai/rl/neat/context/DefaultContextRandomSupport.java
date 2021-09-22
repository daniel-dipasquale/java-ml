package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class DefaultContextRandomSupport implements Context.RandomSupport {
    private ObjectProfile<RandomSupport> nextIndex;
    private ObjectProfile<RandomSupport> isLessThan;

    @Override
    public int generateIndex(final int offset, final int count) {
        return nextIndex.getObject().next(offset, count);
    }

    @Override
    public boolean isLessThan(final float rate) {
        return isLessThan.getObject().isLessThan(rate);
    }

    public void save(final SerializableStateGroup state) {
        state.put("random.nextIndex", nextIndex);
        state.put("random.isLessThan", isLessThan);
    }

    public void load(final SerializableStateGroup state, final IterableEventLoop eventLoop) {
        nextIndex = ObjectProfile.switchProfile(state.get("random.nextIndex"), eventLoop != null);
        isLessThan = ObjectProfile.switchProfile(state.get("random.isLessThan"), eventLoop != null);
    }
}
