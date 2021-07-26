package com.dipasquale.ai.rl.neat.switcher;

import com.dipasquale.ai.rl.neat.factory.WeightPerturber;
import com.dipasquale.common.Pair;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.switcher.AbstractObjectSwitcher;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

public final class WeightPerturberSwitcher extends AbstractObjectSwitcher<WeightPerturber> {
    @Serial
    private static final long serialVersionUID = -1787775418318205965L;
    private final Pair<FloatFactory> floatFactoryPair;
    @Getter(AccessLevel.PROTECTED)
    private final WeightPerturber on;
    @Getter(AccessLevel.PROTECTED)
    private final WeightPerturber off;

    public WeightPerturberSwitcher(final boolean isOn, final Pair<FloatFactory> floatFactoryPair) {
        super(isOn);
        this.floatFactoryPair = floatFactoryPair;
        this.on = new OnWeightPerturber();
        this.off = new OffWeightPerturber();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class OnWeightPerturber implements WeightPerturber, Serializable {
        @Serial
        private static final long serialVersionUID = -2645610242809449679L;

        @Override
        public float perturb(final float value) {
            return floatFactoryPair.getLeft().create() * value;
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class OffWeightPerturber implements WeightPerturber, Serializable {
        @Serial
        private static final long serialVersionUID = 1430353314565694939L;

        @Override
        public float perturb(final float value) {
            return floatFactoryPair.getRight().create() * value;
        }
    }
}
