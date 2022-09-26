package com.dipasquale.ai.rl.neat.factory;

import com.dipasquale.common.factory.ObjectFactory;

import java.util.List;

public interface RecurrentWeightFactory extends ObjectFactory<List<Float>> {
    List<Float> clone(List<Float> recurrentWeights);

    List<Float> createAverage(List<Float> recurrentWeights1, List<Float> recurrentWeights2);
}
