package com.dipasquale.common;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@FunctionalInterface
public interface IntegerFactory extends Serializable {
    int create();

    static IntegerFactory createLiteral(final int value) {
        return new IntegerFactory() {
            @Serial
            private static final long serialVersionUID = -7921366360162050840L;

            @Override
            public int create() {
                return value;
            }
        };
    }

    static IntegerFactory createIllegalState(final String message) {
        return new IntegerFactory() {
            @Serial
            private static final long serialVersionUID = -7785976028561926360L;

            @Override
            public int create() {
                throw new IllegalStateException(message);
            }
        };
    }

    static IntegerFactory createCyclic(final List<? extends IntegerFactory> factories, final boolean contended) {
        if (!contended) {
            return new IntegerFactoryCyclic(factories);
        }

        return new IntegerFactoryCyclicCas(factories);
    }

    static IntegerFactory createRandom(final RandomSupportFloat randomSupport, final int min, final int max) {
        return new IntegerFactory() {
            @Serial
            private static final long serialVersionUID = 1728217117556363097L;

            @Override
            public int create() {
                return randomSupport.next(min, max);
            }
        };
    }
}
