package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.common.random.concurrent.RandomBiSupportFloat;
import com.dipasquale.data.structure.map.SerializableInteroperableStateMap;
import com.dipasquale.threading.event.loop.EventLoopIterable;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class ContextDefaultRandomSupport implements Context.RandomSupport {
    private RandomBiSupportFloat nextIndex;
    private RandomBiSupportFloat isLessThan;

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

    private static RandomBiSupportFloat load(final RandomBiSupportFloat randomSupport, final EventLoopIterable eventLoop) {
        return randomSupport.selectContended(eventLoop != null);
    }

    public void load(final SerializableInteroperableStateMap state, final EventLoopIterable eventLoop) {
        nextIndex = load(state.<RandomBiSupportFloat>get("random.nextIndex"), eventLoop);
        isLessThan = load(state.<RandomBiSupportFloat>get("random.isLessThan"), eventLoop);
    }
}
