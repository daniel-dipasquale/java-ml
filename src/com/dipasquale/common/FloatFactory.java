package com.dipasquale.common;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@FunctionalInterface
public interface FloatFactory extends Serializable { // TODO: add a differentiator between singleton or transient
    float create();

    static FloatFactory createLiteral(final float value) {
        return new FloatFactory() {
            @Serial
            private static final long serialVersionUID = 890190052327745368L;

            @Override
            public float create() {
                return value;
            }
        };
    }

    static FloatFactory createIllegalState(final String message) {
        return new FloatFactory() {
            @Serial
            private static final long serialVersionUID = -205272143970817133L;

            @Override
            public float create() {
                throw new IllegalStateException(message);
            }
        };
    }

    static FloatFactory createCyclic(final List<? extends FloatFactory> factories, final boolean contended) {
        if (!contended) {
            return new FloatFactoryCyclic(factories);
        }

        return new FloatFactoryCyclicCas(factories);
    }

    static FloatFactory createRandom(final RandomSupportFloat randomSupport, final float min, final float max) {
        return new FloatFactory() {
            @Serial
            private static final long serialVersionUID = 7091995050863927280L;

            @Override
            public float create() {
                return randomSupport.next(min, max);
            }
        };
    }
}
