/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.switcher.factory;

import com.dipasquale.ai.rl.neat.factory.WeightPerturber;
import com.dipasquale.common.Pair;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.switcher.AbstractObjectSwitcher;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

public final class WeightPerturberSwitcher extends AbstractObjectSwitcher<WeightPerturber> {
    @Serial
    private static final long serialVersionUID = -1787775418318205965L;

    public WeightPerturberSwitcher(final boolean isOn, final Pair<FloatFactory> floatFactoryPair) {
        super(isOn, new DefaultWeightPerturber(floatFactoryPair.getLeft()), new DefaultWeightPerturber(floatFactoryPair.getRight()));
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DefaultWeightPerturber implements WeightPerturber, Serializable {
        @Serial
        private static final long serialVersionUID = -2645610242809449679L;
        private final FloatFactory floatFactory;

        @Override
        public float perturb(final float value) {
            return floatFactory.create() * value;
        }
    }
}
