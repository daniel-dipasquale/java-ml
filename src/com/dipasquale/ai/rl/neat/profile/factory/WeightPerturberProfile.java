package com.dipasquale.ai.rl.neat.profile.factory;

import com.dipasquale.ai.rl.neat.factory.WeightPerturber;
import com.dipasquale.common.Pair;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.profile.AbstractObjectProfile;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

public final class WeightPerturberProfile extends AbstractObjectProfile<WeightPerturber> {
    @Serial
    private static final long serialVersionUID = -1787775418318205965L;

    public WeightPerturberProfile(final boolean isOn, final Pair<FloatFactory> floatFactoryPair) {
        super(isOn, new DefaultWeightPerturber(floatFactoryPair.getLeft()), new DefaultWeightPerturber(floatFactoryPair.getRight()));
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DefaultWeightPerturber implements WeightPerturber, Serializable {
        @Serial
        private static final long serialVersionUID = -2645610242809449679L;
        private final FloatFactory floatFactory;

        @Override
        public float perturb(final float value) {
            float perturbed = floatFactory.create() * value;

            if (perturbed == Float.POSITIVE_INFINITY) {
                return Float.MAX_VALUE;
            }

            if (perturbed == Float.NEGATIVE_INFINITY) {
                return Float.MIN_VALUE;
            }

            return perturbed;
        }
    }
}
