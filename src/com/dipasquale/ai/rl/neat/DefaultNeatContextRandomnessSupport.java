package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.random.ProbabilityClassifier;
import com.dipasquale.common.random.RandomSupport;
import com.dipasquale.io.serialization.SerializableStateGroup;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
final class DefaultNeatContextRandomnessSupport implements NeatContext.RandomnessSupport {
    private final RandomSupport generateIndexOrElementRandomSupport;
    private final RandomSupport isLessThanRandomSupport;
    private final RandomSupport shuffleRandomSupport;

    static DefaultNeatContextRandomnessSupport create(final NeatInitializationContext initializationContext) {
        RandomSupport generateIndexOrElementRandomSupport = initializationContext.createDefaultRandomSupport();
        RandomSupport isLessThanRandomSupport = initializationContext.createDefaultRandomSupport();
        RandomSupport shuffleRandomSupport = initializationContext.createDefaultRandomSupport();

        return new DefaultNeatContextRandomnessSupport(generateIndexOrElementRandomSupport, isLessThanRandomSupport, shuffleRandomSupport);
    }

    @Override
    public int generateIndex(final int offset, final int count) {
        return generateIndexOrElementRandomSupport.nextInteger(offset, count);
    }

    @Override
    public boolean isLessThan(final float rate) {
        return isLessThanRandomSupport.isLessThan(rate);
    }

    @Override
    public <T> T generateElement(final ProbabilityClassifier<T> probabilityClassifier) {
        float value = generateIndexOrElementRandomSupport.nextFloat();

        return probabilityClassifier.get(value);
    }

    @Override
    public <T> void shuffle(final List<T> elements) {
        shuffleRandomSupport.shuffle(elements);
    }

    void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("random.generateIndexOrElementRandomSupport", generateIndexOrElementRandomSupport);
        stateGroup.put("random.isLessThanRandomSupport", isLessThanRandomSupport);
        stateGroup.put("random.shuffleRandomSupport", shuffleRandomSupport);
    }

    static DefaultNeatContextRandomnessSupport create(final SerializableStateGroup stateGroup) {
        RandomSupport generateIndexOrElementRandomSupport = stateGroup.get("random.generateIndexOrElementRandomSupport");
        RandomSupport isLessThanRandomSupport = stateGroup.get("random.isLessThanRandomSupport");
        RandomSupport shuffleRandomSupport = stateGroup.get("random.shuffleRandomSupport");

        return new DefaultNeatContextRandomnessSupport(generateIndexOrElementRandomSupport, isLessThanRandomSupport, shuffleRandomSupport);
    }
}
