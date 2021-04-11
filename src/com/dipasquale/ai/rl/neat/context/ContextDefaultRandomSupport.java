package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.common.RandomSupportFloat;
import com.dipasquale.data.structure.map.SerializableInteroperableStateMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public final class ContextDefaultRandomSupport implements Context.RandomSupport {
    private RandomSupportFloat nextIndex;
    private RandomSupportFloat isLessThan;

    @Override
    public int nextIndex(final int offset, final int count) {
        return nextIndex.next(offset, count);
    }

    @Override
    public boolean isLessThan(final float rate) {
        return isLessThan.isLessThan(rate);
    }

    public void save(final SerializableInteroperableStateMap state) {
        state.put("random.nextIndex", nextIndex);
        state.put("random.isLessThan", isLessThan);
    }

    public void load(final SerializableInteroperableStateMap state) {
        nextIndex = state.get("random.nextIndex");
        isLessThan = state.get("random.isLessThan");
    }
}
