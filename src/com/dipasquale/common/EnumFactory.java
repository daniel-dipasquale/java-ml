package com.dipasquale.common;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@FunctionalInterface
public interface EnumFactory<T extends Enum<T>> extends Serializable {
    T create();

    static <T extends Enum<T>> EnumFactory<T> createLiteral(final T value) {
        return new EnumFactory<>() {
            @Serial
            private static final long serialVersionUID = 7867204552397259300L;

            @Override
            public T create() {
                return value;
            }
        };
    }

    static <T extends Enum<T>> EnumFactory<T> createRandom(final RandomSupportFloat randomSupport, final List<T> values) {
        return new EnumFactory<>() {
            @Serial
            private static final long serialVersionUID = -994421340147043154L;

            @Override
            public T create() {
                int index = randomSupport.next(0, values.size());

                return values.get(index);
            }
        };
    }
}
