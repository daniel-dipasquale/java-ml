package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.output.OutputClassifier;
import com.dipasquale.ai.rl.neat.core.InitializationContext;
import com.dipasquale.ai.rl.neat.core.ParallelismSupport;
import com.dipasquale.io.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class DefaultContextRandomSupport implements Context.RandomSupport {
    private DualModeRandomSupport generateIndexOrItemRandomSupport;
    private DualModeRandomSupport isLessThanRandomSupport;

    public static DefaultContextRandomSupport create(final InitializationContext initializationContext) {
        DualModeRandomSupport generateIndexRandomSupport = initializationContext.createDefaultRandomSupport();
        DualModeRandomSupport isLessThanRandomSupport = initializationContext.createDefaultRandomSupport();

        return new DefaultContextRandomSupport(generateIndexRandomSupport, isLessThanRandomSupport);
    }

    @Override
    public int generateIndex(final int offset, final int count) {
        return generateIndexOrItemRandomSupport.next(offset, count);
    }

    @Override
    public boolean isLessThan(final float rate) {
        return isLessThanRandomSupport.isLessThan(rate);
    }

    @Override
    public <T> T generateItem(final OutputClassifier<T> outputClassifier) {
        return outputClassifier.resolve(generateIndexOrItemRandomSupport.next());
    }

    public void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("random.generateIndexOrItemRandomSupport", generateIndexOrItemRandomSupport);
        stateGroup.put("random.isLessThanRandomSupport", isLessThanRandomSupport);
    }

    public void load(final SerializableStateGroup stateGroup, final IterableEventLoop eventLoop) {
        generateIndexOrItemRandomSupport = DualModeObject.activateMode(stateGroup.get("random.generateIndexOrItemRandomSupport"), ParallelismSupport.getConcurrencyLevel(eventLoop));
        isLessThanRandomSupport = DualModeObject.activateMode(stateGroup.get("random.isLessThanRandomSupport"), ParallelismSupport.getConcurrencyLevel(eventLoop));
    }
}
