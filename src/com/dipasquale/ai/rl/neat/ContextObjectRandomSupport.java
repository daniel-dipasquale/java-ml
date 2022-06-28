package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.random.ProbabilityClassifier;
import com.dipasquale.io.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.random.DualModeRandomSupport;
import com.dipasquale.synchronization.event.loop.ParallelEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
final class ContextObjectRandomSupport implements Context.RandomSupport {
    private DualModeRandomSupport generateIndexOrItemRandomSupport;
    private DualModeRandomSupport isLessThanRandomSupport;
    private DualModeRandomSupport shuffleRandomSupport;

    static ContextObjectRandomSupport create(final InitializationContext initializationContext) {
        DualModeRandomSupport generateIndexRandomSupport = initializationContext.createDefaultRandomSupport();
        DualModeRandomSupport isLessThanRandomSupport = initializationContext.createDefaultRandomSupport();
        DualModeRandomSupport shuffleRandomSupport = initializationContext.createDefaultRandomSupport();

        return new ContextObjectRandomSupport(generateIndexRandomSupport, isLessThanRandomSupport, shuffleRandomSupport);
    }

    @Override
    public int generateIndex(final int offset, final int count) {
        return generateIndexOrItemRandomSupport.nextInteger(offset, count);
    }

    @Override
    public boolean isLessThan(final float rate) {
        return isLessThanRandomSupport.isLessThan(rate);
    }

    @Override
    public <T> T generateItem(final ProbabilityClassifier<T> probabilityClassifier) {
        float value = generateIndexOrItemRandomSupport.nextFloat();

        return probabilityClassifier.get(value);
    }

    @Override
    public <T> void shuffle(final List<T> items) {
        shuffleRandomSupport.shuffle(items);
    }

    public void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("random.generateIndexOrItemRandomSupport", generateIndexOrItemRandomSupport);
        stateGroup.put("random.isLessThanRandomSupport", isLessThanRandomSupport);
        stateGroup.put("random.shuffleRandomSupport", shuffleRandomSupport);
    }

    private void load(final SerializableStateGroup stateGroup, final int concurrencyLevel) {
        generateIndexOrItemRandomSupport = DualModeObject.activateMode(stateGroup.get("random.generateIndexOrItemRandomSupport"), concurrencyLevel);
        isLessThanRandomSupport = DualModeObject.activateMode(stateGroup.get("random.isLessThanRandomSupport"), concurrencyLevel);
        shuffleRandomSupport = DualModeObject.activateMode(stateGroup.get("random.shuffleRandomSupport"), concurrencyLevel);
    }

    public void load(final SerializableStateGroup stateGroup, final ParallelEventLoop eventLoop) {
        load(stateGroup, ParallelismSupport.getConcurrencyLevel(eventLoop));
    }
}
