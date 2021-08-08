package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.common.SerializableInteroperableStateMap;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.switcher.ObjectSwitcher;
import com.dipasquale.threading.event.loop.IterableEventLoop;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class DefaultRandomSupportContext implements Context.RandomSupport {
    private ObjectSwitcher<RandomSupport> nextIndex;
    private ObjectSwitcher<RandomSupport> isLessThan;

    @Override
    public int nextIndex(final int offset, final int count) {
        return nextIndex.getObject().next(offset, count);
    }

    @Override
    public boolean isLessThan(final float rate) {
        return isLessThan.getObject().isLessThan(rate);
    }

    public void save(final SerializableInteroperableStateMap state) {
        state.put("random.nextIndex", nextIndex);
        state.put("random.isLessThan", isLessThan);
    }

    public void load(final SerializableInteroperableStateMap state, final IterableEventLoop eventLoop) {
        nextIndex = ObjectSwitcher.switchObject(state.get("random.nextIndex"), eventLoop != null);
        isLessThan = ObjectSwitcher.switchObject(state.get("random.isLessThan"), eventLoop != null);
    }
}
